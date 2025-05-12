package com.example.jobportal.controller;

import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.User;
import com.example.jobportal.service.CandidateService;
import com.example.jobportal.service.SkillService;
import com.example.jobportal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;
    private final UserService userService;
    private final SkillService skillService;

    @Autowired
    public CandidateController(CandidateService candidateService, UserService userService, SkillService skillService) {
        this.candidateService = candidateService;
        this.userService = userService;
        this.skillService = skillService;
    }

    @GetMapping
    public String listCandidates(Model model) {
        try {
            List<Candidate> candidates = candidateService.findAll();
            model.addAttribute("candidates", candidates);
            return "candidates/list";
        } catch (Exception e) {
            logger.error("Error listing candidates", e);
            model.addAttribute("error", "Error loading candidates");
            return "candidates/list";
        }
    }

    @GetMapping("/create")
    public String createCandidateForm(Model model) {
        try {
            // Create a simple data map instead of the entity
            Map<String, Object> candidateData = new HashMap<>();
            candidateData.put("candidateId", null);
            candidateData.put("location", "");
            candidateData.put("yearsExperience", 0);
            candidateData.put("skills", List.of());

            model.addAttribute("candidate", candidateData);
            model.addAttribute("skillsList", skillService.findAll().stream()
                    .map(skill -> Map.of("id", skill.getId(), "name", skill.getName()))
                    .collect(Collectors.toList()));

            List<User> candidateUsers = userService.findAll().stream()
                    .filter(user -> user.getType() == User.UserType.CANDIDATE)
                    .collect(Collectors.toList());
            model.addAttribute("users", candidateUsers);

            return "candidates/form";
        } catch (Exception e) {
            logger.error("Error creating candidate form", e);
            return "redirect:/candidates?error=formError";
        }
    }

    @GetMapping("/{id}/edit")
    public String editCandidateForm(@PathVariable Long id, Model model) {
        try {
            candidateService.findById(id).ifPresentOrElse(
                    candidate -> {
                        // Create a DTO to avoid serialization issues
                        Map<String, Object> candidateData = new HashMap<>();
                        candidateData.put("candidateId", candidate.getCandidateId());
                        candidateData.put("location", candidate.getLocation());
                        candidateData.put("yearsExperience", candidate.getYearsExperience());

                        // Map skills to simple objects
                        List<Map<String, Object>> skillsData = candidate.getSkills().stream()
                                .map(cs -> {
                                    Map<String, Object> skillData = new HashMap<>();
                                    skillData.put("skill", Map.of(
                                            "id", cs.getSkill().getId(),
                                            "name", cs.getSkill().getName()
                                    ));
                                    skillData.put("proficiency", cs.getProficiency());
                                    return skillData;
                                })
                                .collect(Collectors.toList());
                        candidateData.put("skills", skillsData);

                        // User data
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("id", candidate.getUser().getId());
                        userData.put("name", candidate.getUser().getName());
                        userData.put("email", candidate.getUser().getEmail());
                        candidateData.put("user", userData);

                        model.addAttribute("candidate", candidateData);
                        model.addAttribute("skillsList", skillService.findAll().stream()
                                .map(skill -> Map.of("id", skill.getId(), "name", skill.getName()))
                                .collect(Collectors.toList()));
                    },
                    () -> model.addAttribute("error", "Candidate not found")
            );

            List<User> candidateUsers = userService.findAll().stream()
                    .filter(user -> user.getType() == User.UserType.CANDIDATE)
                    .collect(Collectors.toList());
            model.addAttribute("users", candidateUsers);

            return "candidates/form";
        } catch (Exception e) {
            logger.error("Error loading candidate for edit", e);
            return "redirect:/candidates?error=loadError";
        }
    }

    @PostMapping
    public String saveCandidate(@RequestParam(name = "candidateId", required = false) Long candidateId,
                                @RequestParam(name = "userId", required = false) Long userId,
                                @RequestParam(name = "location") String location,
                                @RequestParam(name = "yearsExperience") Integer yearsExperience,
                                @RequestParam(name = "skillIds", required = false) List<Long> skillIds,
                                @RequestParam(name = "proficiencies", required = false) List<Integer> proficiencies,
                                RedirectAttributes redirectAttributes) {
        try {
            Candidate candidateData = new Candidate();
            candidateData.setCandidateId(candidateId);
            candidateData.setLocation(location);
            candidateData.setYearsExperience(yearsExperience);

            Candidate savedCandidate;

            if (candidateId == null) {
                if (userId == null) {
                    redirectAttributes.addFlashAttribute("error", "Please select a user for the candidate");
                    return "redirect:/candidates/create";
                }
                savedCandidate = candidateService.save(candidateData, userId);
            } else {
                savedCandidate = candidateService.save(candidateData, null);
            }

            if (skillIds != null && !skillIds.isEmpty() && proficiencies != null) {
                candidateService.updateCandidateSkills(savedCandidate.getCandidateId(), skillIds, proficiencies);
            } else if (candidateId != null) {
                candidateService.clearCandidateSkills(savedCandidate.getCandidateId());
            }

            redirectAttributes.addFlashAttribute("success", "Candidate saved successfully");
            return "redirect:/candidates";

        } catch (Exception e) {
            logger.error("Error saving candidate", e);
            redirectAttributes.addFlashAttribute("error", "Failed to save candidate: " + e.getMessage());

            if (candidateId == null) {
                return "redirect:/candidates/create";
            } else {
                return "redirect:/candidates/" + candidateId + "/edit";
            }
        }
    }

    @GetMapping("/{id}")
    public String viewCandidate(@PathVariable Long id, Model model) {
        candidateService.findById(id).ifPresent(candidate -> model.addAttribute("candidate", candidate));
        return "candidates/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteCandidate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidateService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Candidate deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting candidate", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete candidate: " + e.getMessage());
        }
        return "redirect:/candidates";
    }
}