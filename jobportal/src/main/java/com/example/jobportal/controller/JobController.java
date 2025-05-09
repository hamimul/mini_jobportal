package com.example.jobportal.controller;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;
    private final EmployerService employerService;

    @Autowired
    public JobController(JobService jobService, EmployerService employerService) {
        this.jobService = jobService;
        this.employerService = employerService;
    }

    @GetMapping
    public String listJobs(Model model) {
        List<Job> jobs = jobService.findAll();
        model.addAttribute("jobs", jobs);
        return "jobs/list";
    }

    @GetMapping("/create")
    public String createJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("employers", employerService.findAll());
        model.addAttribute("isNew", true);
        return "jobs/form";
    }

    @PostMapping
    public String saveJob(@ModelAttribute Job job,
                          BindingResult bindingResult,
                          @RequestParam Long employerId,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employers", employerService.findAll());
            return "jobs/form";
        }

        try {
            jobService.save(job, employerId);
            redirectAttributes.addFlashAttribute("successMessage", "Job posting saved successfully!");
            return "redirect:/jobs";
        } catch (Exception e) {
            logger.error("Error saving job", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs/create";
        }
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.getById(id);
            model.addAttribute("job", job);
            return "jobs/view";
        } catch (ResourceNotFoundException e) {
            logger.error("Job not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs";
        }
    }

    @GetMapping("/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.getById(id);
            model.addAttribute("job", job);
            model.addAttribute("employers", employerService.findAll());
            model.addAttribute("isNew", false);
            return "jobs/form";
        } catch (ResourceNotFoundException e) {
            logger.error("Job not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting job with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/jobs";
    }

    @GetMapping("/employer/{employerId}")
    public String listJobsByEmployer(@PathVariable Long employerId, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Job> jobs = jobService.findByEmployerId(employerId);
            Employer employer = employerService.getById(employerId);
            model.addAttribute("jobs", jobs);
            model.addAttribute("employer", employer);
            return "jobs/employer-jobs";
        } catch (ResourceNotFoundException e) {
            logger.error("Employer not found with ID: {}", employerId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employers";
        }
    }
}