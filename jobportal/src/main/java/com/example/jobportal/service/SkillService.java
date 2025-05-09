package com.example.jobportal.service;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.model.Skill;
import com.example.jobportal.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> findAll() {
        try {
            return skillRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all skills", e);
            throw new ServiceException("Failed to retrieve all skills", e);
        }
    }

    public Optional<Skill> findById(Long id) {
        try {
            return skillRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching skill with ID: {}", id, e);
            throw new ServiceException("Failed to retrieve skill with ID: " + id, e);
        }
    }

    public Skill getById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
    }

    public Skill findByName(String name) {
        try {
            Skill skill = skillRepository.findByName(name);
            if (skill == null) {
                throw new ResourceNotFoundException("Skill", "name", name);
            }
            return skill;
        } catch (DataAccessException e) {
            logger.error("Error fetching skill with name: {}", name, e);
            throw new ServiceException("Failed to retrieve skill with name: " + name, e);
        }
    }

    @Transactional
    public Skill save(Skill skill) {
        try {
            // Validate skill data
            if (skill.getName() == null || skill.getName().trim().isEmpty()) {
                throw new ServiceException("Skill name cannot be empty");
            }

            return skillRepository.save(skill);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving skill - duplicate name: {}", skill.getName(), e);
            throw new ServiceException("A skill with this name already exists", e);
        } catch (DataAccessException e) {
            logger.error("Error saving skill: {}", skill, e);
            throw new ServiceException("Failed to save skill", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!skillRepository.existsById(id)) {
                throw new ResourceNotFoundException("Skill", "id", id);
            }
            skillRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            logger.error("Cannot delete skill with ID: {} as it is referenced by other entities", id, e);
            throw new ServiceException("Cannot delete this skill as it is being used by jobs or candidates", e);
        } catch (DataAccessException e) {
            logger.error("Error deleting skill with ID: {}", id, e);
            throw new ServiceException("Failed to delete skill with ID: " + id, e);
        }
    }
}