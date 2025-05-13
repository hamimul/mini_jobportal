package com.example.jobportal.controller;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.dto.JobMatchDTO;
import com.example.jobportal.service.CandidateService;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.MatchmakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/matching")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;
    private final JobService jobService;
    private final EmployerService employerService;
    private final CandidateService candidateService;

    @Autowired
    public MatchmakingController(
            MatchmakingService matchmakingService,
            JobService jobService,
            EmployerService employerService,
            CandidateService candidateService) {
        this.matchmakingService = matchmakingService;
        this.jobService = jobService;
        this.employerService = employerService;
        this.candidateService = candidateService;
    }

    @GetMapping("/jobs/{jobId}")
    public String showJobMatches(@PathVariable Long jobId, Model model) {
        List<CandidateMatchDTO> matches = matchmakingService.findMatchesForJob(jobId);
        jobService.findById(jobId).ifPresent(job -> model.addAttribute("job", job));
        model.addAttribute("matches", matches);
        return "matching/job-matches";
    }

    @GetMapping("/jobs/{jobId}/paged")
    public String showJobMatchesPaged(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minScore,
            Model model) {

        Map<String, String> filters = new HashMap<>();
        if (location != null && !location.isEmpty()) {
            filters.put("location", location);
        }
        if (minExperience != null) {
            filters.put("minExperience", minExperience.toString());
        }
        if (minScore != null) {
            filters.put("minScore", minScore.toString());
        }

        List<CandidateMatchDTO> matches = matchmakingService.findMatchesForJobWithPagination(jobId, page, size, filters);
        jobService.findById(jobId).ifPresent(job -> model.addAttribute("job", job));
        model.addAttribute("matches", matches);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "matching/job-matches-paged";
    }

    @GetMapping("/employers/{employerId}")
    public String showEmployerDashboard(@PathVariable Long employerId, Model model) {
        List<CandidateMatchDTO> topMatches = matchmakingService.findTopMatchesForEmployer(employerId, 5);
        employerService.findById(employerId).ifPresent(employer -> model.addAttribute("employer", employer));
        model.addAttribute("topMatches", topMatches);
        return "matching/employer-dashboard";
    }

    @GetMapping("/candidates/{candidateId}")
    public String showCandidateMatches(@PathVariable Long candidateId, Model model) {
        List<JobMatchDTO> matchingJobs = matchmakingService.findMatchingJobsForCandidate(candidateId);
        candidateService.findById(candidateId).ifPresent(candidate -> model.addAttribute("candidate", candidate));
        model.addAttribute("matchingJobs", matchingJobs);
        return "matching/candidate-matches";
    }
}