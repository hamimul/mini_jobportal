package com.example.jobportal.service;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.repository.EmployerRepository;
import com.example.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    @Autowired
    public JobService(JobRepository jobRepository, EmployerRepository employerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> findByEmployerId(Long employerId) {
        return jobRepository.findByEmployerEmployerId(employerId);
    }

    @Transactional
    public Job save(Job job, Long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        job.setEmployer(employer);
        return jobRepository.save(job);
    }

    public void deleteById(Long id) {
        jobRepository.deleteById(id);
    }
}