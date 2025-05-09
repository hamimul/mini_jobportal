package com.example.jobportal.controller;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.Employer;
import com.example.jobportal.model.User;
import com.example.jobportal.service.EmployerService;
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
@RequestMapping("/employers")
public class EmployerController {

    private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);

    private final EmployerService employerService;
    private final UserService userService;

    @Autowired
    public EmployerController(EmployerService employerService, UserService userService) {
        this.employerService = employerService;
        this.userService = userService;
    }

    @GetMapping
    public String listEmployers(Model model) {
        List<Employer> employers = employerService.findAll();
        model.addAttribute("employers", employers);
        return "employers/list";
    }

    @GetMapping("/create")
    public String createEmployerForm(Model model) {
        model.addAttribute("employer", new Employer());
        List<User> employerUsers = userService.findAll().stream()
                .filter(user -> user.getType() == User.UserType.EMPLOYER)
                .toList();
        model.addAttribute("users", employerUsers);
        model.addAttribute("isNew", true);
        return "employers/form";
    }

    // In EmployerController.java
    @PostMapping
    public String saveEmployer(@ModelAttribute Employer employer,
                               @RequestParam Long userId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            // Basic validation
            if (employer.getCompanyName() == null || employer.getCompanyName().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Company name is required");
                model.addAttribute("employer", employer);
                List<User> employerUsers = userService.findAll().stream()
                        .filter(user -> user.getType() == User.UserType.EMPLOYER)
                        .toList();
                model.addAttribute("users", employerUsers);
                return "employers/form";
            }

            employerService.save(employer, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Employer profile saved successfully!");
            return "redirect:/employers";
        } catch (Exception e) {
            logger.error("Error saving employer", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employers/create";
        }
    }
}