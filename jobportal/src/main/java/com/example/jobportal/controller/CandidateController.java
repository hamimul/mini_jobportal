package com.example.jobportal.controller;

import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.User;
import com.example.jobportal.service.CandidateService;
import com.example.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

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
        return "candidates/form";
    }

    @PostMapping
    public String saveCandidate(@ModelAttribute Candidate candidate,
                                @RequestParam(name = "userId", required = false) Long userId) {
        if (candidate.getCandidateId() == null) {
            // Creating a new candidate
            if (userId == null) {
                return "redirect:/candidates/create?error=userRequired";
            }
            candidateService.save(candidate, userId);
        } else {
            // Updating an existing candidate
            candidateService.save(candidate, userId);
        }
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String viewCandidate(@PathVariable Long id, Model model) {
        candidateService.findById(id).ifPresent(candidate -> model.addAttribute("candidate", candidate));
        return "candidates/view";
    }

    @GetMapping("/{id}/edit")
    public String editCandidateForm(@PathVariable Long id, Model model) {
        candidateService.findById(id).ifPresent(candidate -> {
            model.addAttribute("candidate", candidate);
            // Pre-select the current user in the dropdown
            model.addAttribute("selectedUserId", candidate.getUser().getId());
        });

        // Add users for potential user change
        List<User> candidateUsers = userService.findAll().stream()
                .filter(user -> user.getType() == User.UserType.CANDIDATE)
                .toList();
        model.addAttribute("users", candidateUsers);
        return "candidates/form";
    }

    @GetMapping("/{id}/delete")
    public String deleteCandidate(@PathVariable Long id) {
        candidateService.deleteById(id);
        return "redirect:/candidates";
    }
}