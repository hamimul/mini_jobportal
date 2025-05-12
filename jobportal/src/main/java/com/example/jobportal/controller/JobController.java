package com.example.jobportal.controller;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.JobSkill;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final EmployerService employerService;
    private final SkillService skillService;

    @Autowired
    public JobController(JobService jobService, EmployerService employerService, SkillService skillService) {
        this.jobService = jobService;
        this.employerService = employerService;
        this.skillService = skillService;
    }

    @GetMapping
    public String listJobs(Model model) {
        List<Job> jobs = jobService.findAll();
        model.addAttribute("jobs", jobs);
        return "jobs/list";
    }

    @GetMapping("/create")
    public String createJobForm(Model model) {
        Job job = new Job();
        // Initialize the required skills collection with an empty list
        if (job.getRequiredSkills() == null) {
            job.setRequiredSkills(new HashSet<>());
        }
        model.addAttribute("job", job);
        model.addAttribute("employers", employerService.findAll());
        model.addAttribute("skillsList", skillService.findAll());
        return "jobs/form";
    }

    @PostMapping
    public String saveJob(@ModelAttribute Job job,
                          @RequestParam Long employerId,
                          @RequestParam(value = "skillIds", required = false) List<Long> skillIds,
                          @RequestParam(value = "skillImportance", required = false) List<Integer> skillImportance) {

        // Clear existing required skills (important for updates)
        job.getRequiredSkills().clear();

        // Add skills if provided
        if (skillIds != null && skillImportance != null) {
            for (int i = 0; i < skillIds.size(); i++) {
                if (skillIds.get(i) != null) {
                    JobSkill jobSkill = new JobSkill();
                    JobSkill.JobSkillId id = new JobSkill.JobSkillId();
                    id.setJobId(job.getId()); // This will be set after save
                    id.setSkillId(skillIds.get(i));
                    jobSkill.setId(id);
                    jobSkill.setJob(job);
                    jobSkill.setSkill(skillService.findById(skillIds.get(i)).orElse(null));
                    jobSkill.setImportance(skillImportance.get(i));
                    job.getRequiredSkills().add(jobSkill);
                }
            }
        }

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
        jobService.findById(id).ifPresent(job -> {
            model.addAttribute("job", job);
            model.addAttribute("employers", employerService.findAll());
            model.addAttribute("skillsList", skillService.findAll());
        });
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