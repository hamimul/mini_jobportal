-- Insert sample data
-- Skills
INSERT INTO skills (name) VALUES
                              ('Java'), ('Python'), ('SQL'), ('JavaScript'), ('HTML/CSS'),
                              ('Spring Boot'), ('React'), ('Angular'), ('Node.js'), ('AWS'),
                              ('Docker'), ('Kubernetes'), ('Git'), ('Agile'), ('DevOps');

-- Users (Employers)
INSERT INTO users (name, email, type) VALUES
                                          ('Tech Innovations', 'hr@techinnovations.com', 'EMPLOYER'),
                                          ('DataDrive Solutions', 'careers@datadrive.com', 'EMPLOYER'),
                                          ('Cloud Systems Inc', 'jobs@cloudsystems.com', 'EMPLOYER');

-- Employers
INSERT INTO employers (user_id, company_name, industry) VALUES
                                                            (1, 'Tech Innovations', 'Software Development'),
                                                            (2, 'DataDrive Solutions', 'Data Analytics'),
                                                            (3, 'Cloud Systems Inc', 'Cloud Infrastructure');

-- Users (Candidates)
INSERT INTO users (name, email, type) VALUES
                                          ('John Smith', 'john.smith@example.com', 'CANDIDATE'),
                                          ('Sarah Johnson', 'sarah.j@example.com', 'CANDIDATE'),
                                          ('Michael Wong', 'michael.w@example.com', 'CANDIDATE'),
                                          ('Emily Davis', 'emily.d@example.com', 'CANDIDATE'),
                                          ('Robert Chen', 'robert.c@example.com', 'CANDIDATE');

-- Candidates
INSERT INTO candidates (user_id, location, years_experience) VALUES
                                                                 (4, 'New York', 5),
                                                                 (5, 'San Francisco', 3),
                                                                 (6, 'New York', 7),
                                                                 (7, 'Chicago', 2),
                                                                 (8, 'San Francisco', 6);

-- Candidate Skills
INSERT INTO candidate_skills (candidate_id, skill_id, proficiency) VALUES
                                                                       (4, 1, 4), (4, 3, 5), (4, 6, 3), (4, 10, 2), (4, 13, 4),
                                                                       (5, 2, 5), (5, 3, 4), (5, 5, 3), (5, 7, 5), (5, 13, 3),
                                                                       (6, 1, 5), (6, 3, 4), (6, 6, 5), (6, 9, 3), (6, 11, 4),
                                                                       (7, 2, 3), (7, 4, 4), (7, 5, 5), (7, 8, 3), (7, 14, 2),
                                                                       (8, 1, 4), (8, 4, 5), (8, 6, 4), (8, 10, 5), (8, 11, 3);

-- Jobs
INSERT INTO jobs (employer_id, title, location, min_experience) VALUES
                                                                    (1, 'Senior Java Developer', 'New York', 5),
                                                                    (1, 'Front-end Developer', 'Chicago', 3),
                                                                    (2, 'Data Engineer', 'San Francisco', 4),
                                                                    (2, 'Full Stack Developer', 'New York', 3),
                                                                    (3, 'DevOps Engineer', 'San Francisco', 5);

-- Job Skills
INSERT INTO job_skills (job_id, skill_id, importance) VALUES
                                                          (1, 1, 5), (1, 3, 4), (1, 6, 5), (1, 11, 3), (1, 13, 3),
                                                          (2, 4, 5), (2, 5, 5), (2, 7, 4), (2, 8, 3), (2, 13, 2),
                                                          (3, 2, 5), (3, 3, 5), (3, 9, 3), (3, 10, 4), (3, 11, 3),
                                                          (4, 1, 4), (4, 3, 4), (4, 4, 5), (4, 6, 4), (4, 7, 5),
                                                          (5, 10, 5), (5, 11, 5), (5, 12, 4), (5, 14, 3), (5, 15, 5);