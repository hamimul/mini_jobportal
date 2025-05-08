package com.example.jobportal.controller;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

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
        return "jobs/form";
    }

    @PostMapping
    public String saveJob(@ModelAttribute Job job, @RequestParam Long employerId) {
        jobService.save(job, employerId);
        return "redirect:/jobs";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        jobService.findById(id).ifPresent(job -> model.addAttribute("job", job));
        return "jobs/view";
    }

    @GetMapping("/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model) {
        jobService.findById(id).ifPresent(job -> model.addAttribute("job", job));
        model.addAttribute("employers", employerService.findAll());
        return "jobs/form";
    }

    @GetMapping("/{id}/delete")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return "redirect:/jobs";
    }

    @GetMapping("/employer/{employerId}")
    public String listJobsByEmployer(@PathVariable Long employerId, Model model) {
        List<Job> jobs = jobService.findByEmployerId(employerId);
        model.addAttribute("jobs", jobs);
        employerService.findById(employerId).ifPresent(employer -> model.addAttribute("employer", employer));
        return "jobs/employer-jobs";
    }
}