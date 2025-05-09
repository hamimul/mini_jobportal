package com.example.jobportal.service;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.repository.EmployerRepository;
import com.example.jobportal.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    @Autowired
    public JobService(JobRepository jobRepository, EmployerRepository employerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
    }

    public List<Job> findAll() {
        try {
            return jobRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all jobs", e);
            throw new ServiceException("Failed to retrieve all jobs", e);
        }
    }

    public Optional<Job> findById(Long id) {
        try {
            return jobRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching job with ID: {}", id, e);
            throw new ServiceException("Failed to retrieve job with ID: " + id, e);
        }
    }

    public Job getById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
    }

    public List<Job> findByEmployerId(Long employerId) {
        try {
            return jobRepository.findByEmployerEmployerId(employerId);
        } catch (DataAccessException e) {
            logger.error("Error fetching jobs for employer ID: {}", employerId, e);
            throw new ServiceException("Failed to retrieve jobs for employer ID: " + employerId, e);
        }
    }

    @Transactional
    public Job save(Job job, Long employerId) {
        try {
            Employer employer = employerRepository.findById(employerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", employerId));
            job.setEmployer(employer);

            // Validate job data
            if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
                throw new ServiceException("Job title cannot be empty");
            }

            if (job.getMinExperience() == null || job.getMinExperience() < 0) {
                job.setMinExperience(0); // Set default value
            }

            return jobRepository.save(job);
        } catch (DataAccessException e) {
            logger.error("Error saving job for employer ID: {}", employerId, e);
            throw new ServiceException("Failed to save job", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!jobRepository.existsById(id)) {
                throw new ResourceNotFoundException("Job", "id", id);
            }
            jobRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error deleting job with ID: {}", id, e);
            throw new ServiceException("Failed to delete job with ID: " + id, e);
        }
    }
}