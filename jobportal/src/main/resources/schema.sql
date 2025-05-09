-- Drop existing tables if they exist
DROP TABLE IF EXISTS job_skills;
DROP TABLE IF EXISTS candidate_skills;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS employers;
DROP TABLE IF EXISTS candidates;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS users;

-- Create tables
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       type VARCHAR(20) NOT NULL
);

CREATE TABLE candidates (
                            candidate_id BIGINT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            location VARCHAR(255),
                            years_experience INT,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE employers (
                           employer_id BIGINT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           company_name VARCHAR(255) NOT NULL,
                           industry VARCHAR(255),
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE jobs (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      employer_id BIGINT NOT NULL,
                      title VARCHAR(255) NOT NULL,
                      location VARCHAR(255),
                      min_experience INT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (employer_id) REFERENCES employers(employer_id) ON DELETE CASCADE
);

CREATE TABLE skills (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE candidate_skills (
                                  candidate_id BIGINT NOT NULL,
                                  skill_id BIGINT NOT NULL,
                                  proficiency INT NOT NULL,
                                  PRIMARY KEY (candidate_id, skill_id),
                                  FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
                                  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE TABLE job_skills (
                            job_id BIGINT NOT NULL,
                            skill_id BIGINT NOT NULL,
                            importance INT NOT NULL,
                            PRIMARY KEY (job_id, skill_id),
                            FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
                            FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_candidate_location ON candidates(location);
CREATE INDEX idx_employer_company ON employers(company_name);
CREATE INDEX idx_job_employer ON jobs(employer_id);
CREATE INDEX idx_job_location ON jobs(location);
CREATE INDEX idx_job_created_at ON jobs(created_at);
CREATE INDEX idx_candidate_skill ON candidate_skills(candidate_id, skill_id);
CREATE INDEX idx_job_skill ON job_skills(job_id, skill_id);