package com.example.jobportal.controller;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.model.User;
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
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isNew", true);
        return "users/form";
    }

    @PostMapping
    public String saveUser(@ModelAttribute User user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "users/form";
        }

        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User saved successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Error saving user", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("isNew", false);
            return "users/form";
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return "redirect:/users";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}