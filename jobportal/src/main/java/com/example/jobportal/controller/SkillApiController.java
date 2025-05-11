package com.example.jobportal.controller;

import com.example.jobportal.model.Skill;
import com.example.jobportal.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillApiController {

    private final SkillService skillService;

    @Autowired
    public SkillApiController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.findAll();
    }
}