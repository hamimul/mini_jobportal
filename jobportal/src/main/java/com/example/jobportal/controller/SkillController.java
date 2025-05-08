package com.example.jobportal.controller;

import com.example.jobportal.model.Skill;
import com.example.jobportal.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public String listSkills(Model model) {
        List<Skill> skills = skillService.findAll();
        model.addAttribute("skills", skills);
        return "skills/list";
    }

    @GetMapping("/create")
    public String createSkillForm(Model model) {
        model.addAttribute("skill", new Skill());
        return "skills/form";
    }

    @PostMapping
    public String saveSkill(@ModelAttribute Skill skill) {
        skillService.save(skill);
        return "redirect:/skills";
    }

    @GetMapping("/{id}/edit")
    public String editSkillForm(@PathVariable Long id, Model model) {
        skillService.findById(id).ifPresent(skill -> model.addAttribute("skill", skill));
        return "skills/form";
    }

    @GetMapping("/{id}/delete")
    public String deleteSkill(@PathVariable Long id) {
        skillService.deleteById(id);
        return "redirect:/skills";
    }
}