package com.example.jobportal.controller;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.Skill;
import com.example.jobportal.service.SkillService;
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
@RequestMapping("/skills")
public class SkillController {

    private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

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
        model.addAttribute("isNew", true);
        return "skills/form";
    }

    @PostMapping
    public String saveSkill( @ModelAttribute Skill skill,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "skills/form";
        }

        try {
            skillService.save(skill);
            redirectAttributes.addFlashAttribute("successMessage", "Skill saved successfully!");
            return "redirect:/skills";
        } catch (Exception e) {
            logger.error("Error saving skill", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/skills/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editSkillForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Skill skill = skillService.getById(id);
            model.addAttribute("skill", skill);
            model.addAttribute("isNew", false);
            return "skills/form";
        } catch (ResourceNotFoundException e) {
            logger.error("Skill not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/skills";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteSkill(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            skillService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Skill deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting skill with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/skills";
    }
}