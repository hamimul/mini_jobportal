package com.example.jobportal.service;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.repository.EmployerRepository;
import com.example.jobportal.repository.JobMatchRepository;
import com.example.jobportal.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MatchmakingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingService.class);

    private final JobMatchRepository jobMatchRepository;
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    @Autowired
    public MatchmakingService(
            JobMatchRepository jobMatchRepository,
            JobRepository jobRepository,
            EmployerRepository employerRepository) {
        this.jobMatchRepository = jobMatchRepository;
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
    }

    public List<CandidateMatchDTO> findMatchesForJob(Long jobId) {
        try {
            // Verify job exists
            if (!jobRepository.existsById(jobId)) {
                throw new ResourceNotFoundException("Job", "id", jobId);
            }

            return jobMatchRepository.findMatchingCandidatesForJob(jobId);
        } catch (DataAccessException e) {
            logger.error("Error finding matches for job ID: {}", jobId, e);
            throw new ServiceException("Failed to find candidate matches for job ID: " + jobId, e);
        }
    }

    public List<CandidateMatchDTO> findTopMatchesForEmployer(Long employerId, Integer limit) {
        try {
            // Verify employer exists
            if (!employerRepository.existsById(employerId)) {
                throw new ResourceNotFoundException("Employer", "id", employerId);
            }

            if (limit == null || limit <= 0) {
                limit = 5; // Default value
            }

            return jobMatchRepository.findTopMatchesForEmployer(employerId, limit);
        } catch (DataAccessException e) {
            logger.error("Error finding top matches for employer ID: {}", employerId, e);
            throw new ServiceException("Failed to find top candidate matches for employer ID: " + employerId, e);
        }
    }

    public List<CandidateMatchDTO> findMatchesForJobWithPagination(
            Long jobId,
            Integer page,
            Integer size,
            Map<String, String> filters) {
        try {
            // Verify job exists
            if (!jobRepository.existsById(jobId)) {
                throw new ResourceNotFoundException("Job", "id", jobId);
            }

            // Validate pagination parameters
            if (page == null || page < 0) {
                page = 0;
            }

            if (size == null || size <= 0) {
                size = 10;
            }

            return jobMatchRepository.findMatchingCandidatesWithPagination(jobId, page, size, filters);
        } catch (DataAccessException e) {
            logger.error("Error finding paginated matches for job ID: {}", jobId, e);
            throw new ServiceException("Failed to find paginated candidate matches for job ID: " + jobId, e);
        } catch (Exception e) {
            logger.error("Unexpected error during match pagination for job ID: {}", jobId, e);
            return Collections.emptyList();
        }
    }
}