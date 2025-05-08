# Mini Job Portal with Matchmaking Algorithm

A sophisticated job portal application with an intelligent matchmaking algorithm that connects candidates with job postings based on skills, experience, and location.

## Features

- **User Management**: Support for employer and candidate user types
- **Job Posting**: Employers can create and manage job listings with skill requirements
- **Candidate Profiles**: Candidates can showcase their skills and experience
- **Smart Matchmaking**: Advanced SQL-based matchmaking algorithm
- **Weighted Scoring**: Skills are weighted by importance and proficiency
- **Location Bonuses**: Candidates in the same location receive match bonuses
- **Experience Assessment**: Evaluation of candidates' experience vs. job requirements
- **Detailed Matching Dashboard**: Visual representation of match scores and rankings
- **Filters and Pagination**: Advanced filtering options for better candidate discovery

## Technology Stack

- **Backend**: Java 17, Spring Boot 3
- **Database**: MySQL (with compatibility for H2 and PostgreSQL)
- **Frontend**: Thymeleaf, Bootstrap 5
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Test

## Database Architecture

The application uses a relational database model with the following key entities:

- **Users**: Base entity for all users with common attributes
- **Employers**: Company information and job listings
- **Candidates**: Skills, experience, and location data
- **Jobs**: Job details including required skills and experience
- **Skills**: Reusable skill definitions
- **CandidateSkills**: Many-to-many relationship with proficiency ratings
- **JobSkills**: Many-to-many relationship with importance ratings

## Matchmaking Algorithm

The core of this application is the sophisticated matchmaking algorithm that:

1. **Calculates Skill Coverage**: Matches candidates with at least 60% of required skills
2. **Weights Skills**: Prioritizes skills based on job importance and candidate proficiency
3. **Rewards Location Matches**: Provides a bonus for candidates in the same city
4. **Evaluates Experience**: Compares candidate experience with job requirements
5. **Ranks Candidates**: Provides a ranked list of best-matching candidates for each job

The algorithm uses Common Table Expressions (CTEs), window functions, and advanced SQL features to efficiently compute matches. Performance is optimized with materialized views for pre-computed scores and proper database indexing.

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.8+
- MySQL 8.0+ (or PostgreSQL 13+)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mini-job-portal.git
   cd mini-job-portal
   ```

2. Configure the database connection in application.properties:
  properties# For MySQL
  spring.datasource.url=jdbc:mysql://localhost:3306/jobportal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  spring.datasource.username=your_username
  spring.datasource.password=your_password

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

5. Access the application at
   http://localhost:8080


Usage Examples
Posting a Job

Log in as an employer
Navigate to "Jobs" > "Post New Job"
Fill out the job details including:

Job title and location
Required experience
Required skills with importance ratings



Finding Matching Candidates

Navigate to a job posting
Click on "View Matching Candidates"
Review candidates ranked by match score
Use filters to refine the search
View detailed candidate profiles

Employer Dashboard

Log in as an employer
View the "Employer Dashboard"
See top-matching candidates across all job postings
View statistics and performance metrics
