package com.example.jobportal.service;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.JobSkill;
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

        // First save the job to get the ID
        Job savedJob = jobRepository.save(job);

        // Update the job ID for all required skills
        for (JobSkill skill : savedJob.getRequiredSkills()) {
            skill.getId().setJobId(savedJob.getId());
            skill.setJob(savedJob);
        }

        // Save again with updated skills
        return jobRepository.save(savedJob);
    }

    @Transactional
    public Job update(Job job) {
        Job existingJob = jobRepository.findById(job.getId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Update fields
        existingJob.setTitle(job.getTitle());
        existingJob.setLocation(job.getLocation());
        existingJob.setMinExperience(job.getMinExperience());

        // Clear and update skills
        existingJob.getRequiredSkills().clear();

        for (JobSkill skill : job.getRequiredSkills()) {
            skill.getId().setJobId(existingJob.getId());
            skill.setJob(existingJob);
            existingJob.getRequiredSkills().add(skill);
        }

        return jobRepository.save(existingJob);
    }

    public void deleteById(Long id) {
        jobRepository.deleteById(id);
    }
}