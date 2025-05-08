-- Create materialized view for job-candidate matches
CREATE MATERIALIZED VIEW IF NOT EXISTS job_candidate_matches AS
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
    WHERE ms.matched_skills * 1.0 / jr.total_required_skills >= 0.6
)
SELECT
    job_id,
    candidate_id,
    candidate_name,
    location,
    years_experience,
    (weighted_skill_score + location_bonus + experience_score) AS match_score,
    RANK() OVER (PARTITION BY job_id
               ORDER BY (weighted_skill_score + location_bonus + experience_score) DESC) AS rank
FROM candidate_scores;