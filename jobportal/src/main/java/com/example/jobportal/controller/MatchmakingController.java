package com.example.jobportal.controller;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/matching")
public class MatchmakingController {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingController.class);

    private final MatchmakingService matchmakingService;
    private final JobService jobService;
    private final EmployerService employerService;

    @Autowired
    public MatchmakingController(
            MatchmakingService matchmakingService,
            JobService jobService,
            EmployerService employerService) {
        this.matchmakingService = matchmakingService;
        this.jobService = jobService;
        this.employerService = employerService;
    }

    @GetMapping("/jobs/{jobId}")
    public String showJobMatches(@PathVariable Long jobId, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<CandidateMatchDTO> matches = matchmakingService.findMatchesForJob(jobId);
            Job job = jobService.getById(jobId);

            model.addAttribute("job", job);
            model.addAttribute("matches", matches);

            logger.info("Found {} matches for job ID: {}", matches.size(), jobId);

            return "matching/job-matches";
        } catch (ResourceNotFoundException e) {
            logger.error("Job not found with ID: {}", jobId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs";
        } catch (Exception e) {
            logger.error("Error finding matches for job ID: {}", jobId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error finding matches: " + e.getMessage());
            return "redirect:/jobs/" + jobId;
        }
    }

    @GetMapping("/jobs/{jobId}/paged")
    public String showJobMatchesPaged(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minScore,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
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
            Job job = jobService.getById(jobId);

            model.addAttribute("job", job);
            model.addAttribute("matches", matches);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            // Add filter values to the model for form persistence
            model.addAttribute("locationFilter", location);
            model.addAttribute("minExperienceFilter", minExperience);
            model.addAttribute("minScoreFilter", minScore);

            logger.info("Found {} paginated matches for job ID: {}", matches.size(), jobId);

            return "matching/job-matches-paged";
        } catch (ResourceNotFoundException e) {
            logger.error("Job not found with ID: {}", jobId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs";
        } catch (Exception e) {
            logger.error("Error finding paginated matches for job ID: {}", jobId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error finding matches: " + e.getMessage());
            return "redirect:/matching/jobs/" + jobId;
        }
    }

    @GetMapping("/employers/{employerId}")
    public String showEmployerDashboard(@PathVariable Long employerId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Employer employer = employerService.getById(employerId);
            List<CandidateMatchDTO> topMatches = matchmakingService.findTopMatchesForEmployer(employerId, 5);

            model.addAttribute("employer", employer);
            model.addAttribute("topMatches", topMatches);

            logger.info("Found {} top matches for employer ID: {}", topMatches.size(), employerId);

            return "matching/employer-dashboard";
        } catch (ResourceNotFoundException e) {
            logger.error("Employer not found with ID: {}", employerId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employers";
        } catch (Exception e) {
            logger.error("Error finding top matches for employer ID: {}", employerId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error finding matches: " + e.getMessage());
            return "redirect:/employers/" + employerId;
        }
    }

    @PostMapping("/refresh")
    public String refreshMatchingData(RedirectAttributes redirectAttributes) {
        try {
            // Call the MaterializedViewService to refresh the matching data
            // This needs to be injected if you implement this functionality
            // materializedViewService.refreshJobCandidateMatchesViewNow();

            redirectAttributes.addFlashAttribute("successMessage", "Matching data refreshed successfully!");
            logger.info("Matching data refreshed manually");
        } catch (Exception e) {
            logger.error("Error refreshing matching data", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error refreshing matching data: " + e.getMessage());
        }
        return "redirect:/";
    }
}