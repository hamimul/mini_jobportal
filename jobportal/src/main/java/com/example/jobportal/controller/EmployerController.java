package com.example.jobportal.controller;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.User;
import com.example.jobportal.service.EmployerService;
import com.example.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/employers")
public class EmployerController {

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
        return "employers/form";
    }

    @PostMapping
    public String saveEmployer(@ModelAttribute Employer employer, @RequestParam Long userId) {
        employerService.save(employer, userId);
        return "redirect:/employers";
    }

    @GetMapping("/{id}")
    public String viewEmployer(@PathVariable Long id, Model model) {
        employerService.findById(id).ifPresent(employer -> model.addAttribute("employer", employer));
        return "employers/view";
    }

    @GetMapping("/{id}/edit")
    public String editEmployerForm(@PathVariable Long id, Model model) {
        employerService.findById(id).ifPresent(employer -> model.addAttribute("employer", employer));
        return "employers/form";
    }

    @GetMapping("/{id}/delete")
    public String deleteEmployer(@PathVariable Long id) {
        employerService.deleteById(id);
        return "redirect:/employers";
    }
}