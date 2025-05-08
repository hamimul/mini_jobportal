package com.example.jobportal.service;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.repository.JobMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class MatchmakingService {

    private final JobMatchRepository jobMatchRepository;

    @Autowired
    public MatchmakingService(JobMatchRepository jobMatchRepository) {
        this.jobMatchRepository = jobMatchRepository;
    }

    public List<CandidateMatchDTO> findMatchesForJob(Long jobId) {
        return jobMatchRepository.findMatchingCandidatesForJob(jobId);
    }

    public List<CandidateMatchDTO> findTopMatchesForEmployer(Long employerId, Integer limit) {
        return jobMatchRepository.findTopMatchesForEmployer(employerId, limit);
    }

    public List<CandidateMatchDTO> findMatchesForJobWithPagination(
            Long jobId,
            Integer page,
            Integer size,
            Map<String, String> filters) {
        return jobMatchRepository.findMatchingCandidatesWithPagination(jobId, page, size, filters);
    }
}