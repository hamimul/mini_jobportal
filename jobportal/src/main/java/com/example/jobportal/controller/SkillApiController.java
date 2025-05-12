package com.example.jobportal.controller;

import com.example.jobportal.model.Skill;
import com.example.jobportal.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
public class SkillApiController {

    private final SkillService skillService;

    @Autowired
    public SkillApiController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillDTO> getAllSkills() {
        return skillService.findAll().stream()
                .map(skill -> new SkillDTO(skill.getId(), skill.getName()))
                .collect(Collectors.toList());
    }

    // DTO to avoid lazy loading issues
    public static class SkillDTO {
        private Long id;
        private String name;

        public SkillDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}