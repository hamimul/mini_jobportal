package com.example.jobportal.controller;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.User;
import com.example.jobportal.service.CandidateService;
import com.example.jobportal.service.UserService;
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
@RequestMapping("/candidates")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;
    private final UserService userService;

    @Autowired
    public CandidateController(CandidateService candidateService, UserService userService) {
        this.candidateService = candidateService;
        this.userService = userService;
    }

    @GetMapping
    public String listCandidates(Model model) {
        List<Candidate> candidates = candidateService.findAll();
        model.addAttribute("candidates", candidates);
        return "candidates/list";
    }

    @GetMapping("/create")
    public String createCandidateForm(Model model) {
        model.addAttribute("candidate", new Candidate());
        List<User> candidateUsers = userService.findAll().stream()
                .filter(user -> user.getType() == User.UserType.CANDIDATE)
                .toList();
        model.addAttribute("users", candidateUsers);
        model.addAttribute("isNew", true);
        return "candidates/form";
    }

    @PostMapping
    public String saveCandidate(@ModelAttribute Candidate candidate,
                                BindingResult bindingResult,
                                @RequestParam Long userId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<User> candidateUsers = userService.findAll().stream()
                    .filter(user -> user.getType() == User.UserType.CANDIDATE)
                    .toList();
            model.addAttribute("users", candidateUsers);
            return "candidates/form";
        }

        try {
            candidateService.save(candidate, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Candidate profile saved successfully!");
            return "redirect:/candidates";
        } catch (Exception e) {
            logger.error("Error saving candidate", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/candidates/create";
        }
    }

    @GetMapping("/{id}")
    public String viewCandidate(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Candidate candidate = candidateService.getById(id);
            model.addAttribute("candidate", candidate);
            return "candidates/view";
        } catch (ResourceNotFoundException e) {
            logger.error("Candidate not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/candidates";
        }
    }

    @GetMapping("/{id}/edit")
    public String editCandidateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Candidate candidate = candidateService.getById(id);
            model.addAttribute("candidate", candidate);
            model.addAttribute("isNew", false);
            return "candidates/form";
        } catch (ResourceNotFoundException e) {
            logger.error("Candidate not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/candidates";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteCandidate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidateService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Candidate deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting candidate with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/candidates";
    }
}