package com.example.jobportal.controller;

import com.example.jobportal.model.Job;
import com.example.jobportal.model.Skill;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final JobService jobService;
    private final SkillService skillService;

    @Autowired
    public MainController(JobService jobService, SkillService skillService) {
        this.jobService = jobService;
        this.skillService = skillService;
    }

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Get latest jobs (limited to 5)
            List<Job> latestJobs = jobService.findAll().stream()
                    .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("latestJobs", latestJobs);
            return "index";
        } catch (Exception e) {
            logger.error("Error loading home page", e);
            model.addAttribute("errorMessage", "Error loading home page: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/api/skills")
    @ResponseBody
    public List<Map<String, Object>> getSkillsList() {
        try {
            // This is a simplified example for Ajax requests to get skills
            // This would normally use a proper SkillDTO
            return skillService.findAll().stream()
                    .map(skill -> {
                        Map<String, Object> skillMap = new HashMap<>();
                        skillMap.put("id", skill.getId());
                        skillMap.put("name", skill.getName());
                        return skillMap;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching skills for API", e);
            return Collections.emptyList();
        }
    }
}