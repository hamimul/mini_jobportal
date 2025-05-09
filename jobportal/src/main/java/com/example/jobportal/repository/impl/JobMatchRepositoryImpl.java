package com.example.jobportal.repository.impl;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.repository.JobMatchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class JobMatchRepositoryImpl implements JobMatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<CandidateMatchDTO> findMatchingCandidatesForJob(Long jobId) {
        String sql = """
            WITH job_requirements AS (
                SELECT j.id AS job_id, j.location AS job_location, j.min_experience,
                       COUNT(js.skill_id) AS total_required_skills
                FROM jobs j
                LEFT JOIN job_skills js ON j.id = js.job_id
                WHERE j.id = :jobId
                GROUP BY j.id, j.location, j.min_experience
            ),
            candidate_skills AS (
                SELECT c.candidate_id, u.name AS candidate_name, c.location,
                       c.years_experience, s.id AS skill_id, cs.proficiency, js.importance
                FROM candidates c
                JOIN users u ON c.user_id = u.id
                JOIN candidate_skills cs ON c.candidate_id = cs.candidate_id
                JOIN skills s ON cs.skill_id = s.id
                LEFT JOIN job_skills js ON s.id = js.skill_id AND js.job_id = :jobId
                WHERE js.job_id = :jobId
            ),
            matching_skills AS (
                SELECT cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience,
                       COUNT(cs.skill_id) AS matched_skills,
                       SUM(cs.proficiency * COALESCE(cs.importance, 0)) AS weighted_skill_score
                FROM candidate_skills cs
                GROUP BY cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience
            ),
            candidate_scores AS (
                SELECT ms.candidate_id, ms.candidate_name, ms.location, ms.years_experience,
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
                CROSS JOIN job_requirements jr
                WHERE ms.matched_skills * 1.0 / jr.total_required_skills >= 0.6
            ),
            final_scores AS (
                SELECT candidate_id, candidate_name, location, years_experience,
                       (weighted_skill_score + location_bonus + experience_score) AS match_score,
                       RANK() OVER (ORDER BY (weighted_skill_score + location_bonus + experience_score) DESC) AS rank
                FROM candidate_scores
            )
            SELECT candidate_id, candidate_name, location, years_experience, match_score, rank
            FROM final_scores
            ORDER BY rank
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("jobId", jobId);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new CandidateMatchDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).doubleValue(),
                        ((Number) row[5]).intValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CandidateMatchDTO> findTopMatchesForEmployer(Long employerId, Integer limit) {
        String sql = """
            WITH employer_jobs AS (
                SELECT j.id AS job_id
                FROM jobs j
                WHERE j.employer_id = :employerId
            ),
            job_requirements AS (
                SELECT j.id AS job_id, j.location AS job_location, j.min_experience,
                       COUNT(js.skill_id) AS total_required_skills
                FROM jobs j
                JOIN employer_jobs ej ON j.id = ej.job_id
                LEFT JOIN job_skills js ON j.id = js.job_id
                GROUP BY j.id, j.location, j.min_experience
            ),
            candidate_skills AS (
                SELECT j.id AS job_id, c.candidate_id, u.name AS candidate_name, 
                       c.location, c.years_experience, s.id AS skill_id, 
                       cs.proficiency, js.importance
                FROM candidates c
                JOIN users u ON c.user_id = u.id
                JOIN candidate_skills cs ON c.candidate_id = cs.candidate_id
                JOIN skills s ON cs.skill_id = s.id
                JOIN job_skills js ON s.id = js.skill_id
                JOIN jobs j ON js.job_id = j.id
                JOIN employer_jobs ej ON j.id = ej.job_id
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
                       CAST(ms.matched_skills AS DOUBLE) / CAST(jr.total_required_skills AS DOUBLE) AS skill_coverage,
                       ms.weighted_skill_score,
                       CASE WHEN ms.location = jr.job_location THEN 10 ELSE 0 END AS location_bonus,
                       CASE 
                           WHEN ms.years_experience < jr.min_experience 
                           THEN (CAST(ms.years_experience AS DOUBLE) / CAST(jr.min_experience AS DOUBLE)) * 5
                           ELSE 5
                       END AS experience_score
                FROM matching_skills ms
                JOIN job_requirements jr ON ms.job_id = jr.job_id
                WHERE CAST(ms.matched_skills AS DOUBLE) / CAST(jr.total_required_skills AS DOUBLE) >= 0.6
            ),
            final_scores AS (
                SELECT job_id, candidate_id, candidate_name, location, years_experience,
                       (weighted_skill_score + location_bonus + experience_score) AS match_score,
                       RANK() OVER (PARTITION BY job_id 
                                   ORDER BY (weighted_skill_score + location_bonus + experience_score) DESC) AS rank_num
                FROM candidate_scores
            )
            SELECT candidate_id, candidate_name, location, years_experience, match_score, rank_num
            FROM final_scores
            WHERE rank_num <= :limit
            ORDER BY job_id, rank_num
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("employerId", employerId);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new CandidateMatchDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).doubleValue(),
                        ((Number) row[5]).intValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CandidateMatchDTO> findMatchingCandidatesWithPagination(
            Long jobId,
            Integer page,
            Integer size,
            Map<String, String> filters) {

        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobId", jobId);
        parameters.put("offset", page * size);
        parameters.put("limit", size);

        // Build the base query
        sqlBuilder.append("""
            WITH job_requirements AS (
                SELECT j.id AS job_id, j.location AS job_location, j.min_experience,
                       COUNT(js.skill_id) AS total_required_skills
                FROM jobs j
                LEFT JOIN job_skills js ON j.id = js.job_id
                WHERE j.id = :jobId
                GROUP BY j.id, j.location, j.min_experience
            ),
            candidate_skills AS (
                SELECT c.candidate_id, u.name AS candidate_name, c.location,
                       c.years_experience, s.id AS skill_id, cs.proficiency, js.importance
                FROM candidates c
                JOIN users u ON c.user_id = u.id
                JOIN candidate_skills cs ON c.candidate_id = cs.candidate_id
                JOIN skills s ON cs.skill_id = s.id
                LEFT JOIN job_skills js ON s.id = js.skill_id AND js.job_id = :jobId
                WHERE js.job_id = :jobId
            ),
            matching_skills AS (
                SELECT cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience,
                       COUNT(cs.skill_id) AS matched_skills,
                       SUM(cs.proficiency * COALESCE(cs.importance, 0)) AS weighted_skill_score
                FROM candidate_skills cs
                GROUP BY cs.candidate_id, cs.candidate_name, cs.location, cs.years_experience
            ),
            candidate_scores AS (
                SELECT ms.candidate_id, ms.candidate_name, ms.location, ms.years_experience,
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
                CROSS JOIN job_requirements jr
                WHERE ms.matched_skills * 1.0 / jr.total_required_skills >= 0.6
            ),
            final_scores AS (
                SELECT candidate_id, candidate_name, location, years_experience,
                       (weighted_skill_score + location_bonus + experience_score) AS match_score,
                       RANK() OVER (ORDER BY (weighted_skill_score + location_bonus + experience_score) DESC) AS rank
                FROM candidate_scores
            """);

        // Apply filters
        if (filters != null && !filters.isEmpty()) {
            sqlBuilder.append(" WHERE 1=1 ");

            if (filters.containsKey("minScore")) {
                sqlBuilder.append(" AND match_score >= :minScore ");
                parameters.put("minScore", Double.parseDouble(filters.get("minScore")));
            }

            if (filters.containsKey("location")) {
                sqlBuilder.append(" AND location = :location ");
                parameters.put("location", filters.get("location"));
            }

            if (filters.containsKey("minExperience")) {
                sqlBuilder.append(" AND years_experience >= :minExperience ");
                parameters.put("minExperience", Integer.parseInt(filters.get("minExperience")));
            }
        }

        sqlBuilder.append("""
            )
            SELECT candidate_id, candidate_name, location, years_experience, match_score, rank
            FROM final_scores
            ORDER BY rank
            LIMIT :limit OFFSET :offset
            """);

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());

        // Set parameters
        parameters.forEach(query::setParameter);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new CandidateMatchDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).doubleValue(),
                        ((Number) row[5]).intValue()
                ))
                .collect(Collectors.toList());
    }
}