package com.example.jobportal.service;

import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.CandidateSkill;
import com.example.jobportal.model.Skill;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.CandidateRepository;
import com.example.jobportal.repository.SkillRepository;
import com.example.jobportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository,
                            UserRepository userRepository,
                            SkillRepository skillRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Transactional
    public Candidate save(Candidate candidate, Long userId) {
        logger.info("Saving candidate with userId: {}, candidateId: {}", userId, candidate.getCandidateId());

        if (candidate.getCandidateId() == null) {
            // Creating a new candidate
            if (userId == null) {
                throw new IllegalArgumentException("User ID must be provided for new candidates");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Check if a candidate already exists for this user
            if (candidateRepository.existsById(userId)) {
                throw new RuntimeException("A candidate already exists for user ID: " + userId);
            }

            Candidate newCandidate = new Candidate();
            newCandidate.setCandidateId(userId);
            newCandidate.setUser(user);
            newCandidate.setLocation(candidate.getLocation());
            newCandidate.setYearsExperience(candidate.getYearsExperience());
            newCandidate.setSkills(new HashSet<>());

            logger.info("Creating new candidate for user: {}", user.getName());
            return candidateRepository.save(newCandidate);
        } else {
            // Updating an existing candidate
            Candidate existingCandidate = candidateRepository.findById(candidate.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + candidate.getCandidateId()));

            // Update only the fields that can be changed
            existingCandidate.setLocation(candidate.getLocation());
            existingCandidate.setYearsExperience(candidate.getYearsExperience());

            logger.info("Updating existing candidate: {}", candidate.getCandidateId());
            return candidateRepository.save(existingCandidate);
        }
    }

    @Transactional
    public void updateCandidateSkills(Long candidateId, List<Long> skillIds, List<Integer> proficiencies) {
        logger.info("Updating skills for candidate: {}", candidateId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + candidateId));

        // Clear existing skills
        candidate.clearSkills();
        entityManager.flush(); // Ensure the clear is persisted

        // Add new skills
        if (skillIds != null && proficiencies != null) {
            for (int i = 0; i < skillIds.size(); i++) {
                if (skillIds.get(i) != null) {
                    Long skillId = skillIds.get(i);
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));

                    CandidateSkill candidateSkill = new CandidateSkill();
                    CandidateSkill.CandidateSkillId id = new CandidateSkill.CandidateSkillId();
                    id.setCandidateId(candidateId);
                    id.setSkillId(skillId);
                    candidateSkill.setId(id);
                    candidateSkill.setCandidate(candidate);
                    candidateSkill.setSkill(skill);
                    candidateSkill.setProficiency(i < proficiencies.size() ? proficiencies.get(i) : 1);

                    candidate.addSkill(candidateSkill);
                    logger.info("Added skill {} with proficiency {} to candidate {}",
                            skill.getName(), candidateSkill.getProficiency(), candidateId);
                }
            }
        }

        candidateRepository.save(candidate);
        logger.info("Successfully updated skills for candidate: {}", candidateId);
    }

    @Transactional
    public void clearCandidateSkills(Long candidateId) {
        logger.info("Clearing skills for candidate: {}", candidateId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + candidateId));
        candidate.clearSkills();
        candidateRepository.save(candidate);

        logger.info("Successfully cleared skills for candidate: {}", candidateId);
    }

    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting candidate with ID: {}", id);
        candidateRepository.deleteById(id);
    }
}