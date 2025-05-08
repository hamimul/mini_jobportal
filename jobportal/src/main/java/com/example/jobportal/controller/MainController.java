package com.example.jobportal.controller;

import com.example.jobportal.model.Job;
import com.example.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final JobService jobService;

    @Autowired
    public MainController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Get latest jobs (limited to 5)
        List<Job> latestJobs = jobService.findAll().stream()
                .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("latestJobs", latestJobs);
        return "index";
    }
}