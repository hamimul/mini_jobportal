package com.example.jobportal.repository;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.dto.JobMatchDTO;
import java.util.List;
import java.util.Map;

public interface JobMatchRepository {
    List<CandidateMatchDTO> findMatchingCandidatesForJob(Long jobId);
    List<CandidateMatchDTO> findTopMatchesForEmployer(Long employerId, Integer limit);
    List<CandidateMatchDTO> findMatchingCandidatesWithPagination(
            Long jobId,
            Integer page,
            Integer size,
            Map<String, String> filters);
    List<JobMatchDTO> findMatchingJobsForCandidate(Long candidateId);
}