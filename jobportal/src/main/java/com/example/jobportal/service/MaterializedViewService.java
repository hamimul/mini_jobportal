package com.example.jobportal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Service
public class MaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MaterializedViewService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    @Scheduled(fixedRate = 3600000) // Refresh every hour
    public void refreshJobCandidateMatchesView() {
        try {
            // For MySQL, we'll drop and recreate the table
            logger.info("Refreshing job candidate matches for MySQL");
            refreshMySqlMatchView();
        } catch (Exception e) {
            logger.error("Error refreshing job candidate matches view", e);
        }
    }

    @Transactional
    public void refreshJobCandidateMatchesViewNow() {
        refreshJobCandidateMatchesView();
    }

    @Transactional
    protected void refreshMySqlMatchView() {
        // For MySQL, drop and recreate the table
        try {
            // First, drop the existing table if it exists
            jdbcTemplate.execute("DROP TABLE IF EXISTS job_candidate_matches");

            // Then create and populate the new table
            jdbcTemplate.execute("""
                CREATE TABLE job_candidate_matches AS
                WITH job_requirements AS (
                    SELECT j.id AS job_id, j.location AS job_location, j.min_experience,
                           COUNT(js.skill_id) AS total_required_skills
                    FROM jobs j
                    LEFT JOIN job_skills js ON j.id = js.job_id
                    GROUP BY j.id, j.location, j.min_experience
                ),
                candidate_skills AS (
                    SELECT j.id AS job_id, c.user_id AS candidate_id, u.name AS candidate_name, 
                           c.location, c.years_experience, s.id AS skill_id, 
                           cs.proficiency, js.importance
                    FROM candidates c
                    JOIN users u ON c.user_id = u.id
                    JOIN candidate_skills cs ON c.user_id = cs.candidate_id
                    JOIN skills s ON cs.skill_id = s.id
                    JOIN job_skills js ON s.id = js.skill_id
                    JOIN jobs j ON js.job_id = j.id
                ),
                matching_skills AS (
                    SELECT cs.job_id, cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience,
                           COUNT(cs.skill_id) AS matched_skills,
                           SUM(cs.proficiency * COALESCE(cs.importance, 0)) AS weighted_skill_score
                    FROM candidate_skills cs
                    GROUP BY cs.job_id, cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience
                ),
                candidate_scores AS (
                    SELECT ms.job_id, ms.candidate_id, ms.candidate_name, ms.location, ms.years_experience,
                           ms.matched_skills,
                           (ms.matched_skills * 1.0 / jr.total_required_skills) AS skill_coverage,
                           ms.weighted_skill_score,
                           CASE WHEN ms.location = jr.job_location THEN 10 ELSE 0 END AS location_bonus,
                           CASE 
                               WHEN ms.years_experience < jr.min_experience 
                               THEN (ms.years_experience * 1.0 / jr.min_experience) * 5
                               ELSE 5
                           END AS experience_score
                    FROM matching_skills ms
                    JOIN job_requirements jr ON ms.job_id = jr.job_id
                    WHERE (ms.matched_skills * 1.0 / jr.total_required_skills) >= 0.6
                )
                SELECT 
                    job_id, 
                    candidate_id, 
                    candidate_name, 
                    location, 
                    years_experience,
                    (weighted_skill_score + location_bonus + experience_score) AS match_score,
                    RANK() OVER (PARTITION BY job_id 
                               ORDER BY (weighted_skill_score + location_bonus + experience_score) DESC) AS rank_num
                FROM candidate_scores
                """);

            // Create indexes on the new table for better performance
            jdbcTemplate.execute("CREATE INDEX idx_job_match_job_id ON job_candidate_matches(job_id)");
            jdbcTemplate.execute("CREATE INDEX idx_job_match_candidate_id ON job_candidate_matches(candidate_id)");
            jdbcTemplate.execute("CREATE INDEX idx_job_match_rank ON job_candidate_matches(rank_num)");

            logger.info("Successfully refreshed job candidate matches for MySQL");
        } catch (Exception e) {
            logger.error("Error refreshing job candidate matches for MySQL", e);
            throw e;
        }
    }
}