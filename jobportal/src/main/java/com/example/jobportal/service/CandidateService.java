package com.example.jobportal.service;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.CandidateRepository;
import com.example.jobportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, UserRepository userRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    public List<Candidate> findAll() {
        try {
            return candidateRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all candidates", e);
            throw new ServiceException("Failed to retrieve all candidates", e);
        }
    }

    public Optional<Candidate> findById(Long id) {
        try {
            return candidateRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching candidate with ID: {}", id, e);
            throw new ServiceException("Failed to retrieve candidate with ID: " + id, e);
        }
    }

    public Candidate getById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));
    }

    @Transactional
    public Candidate save(Candidate candidate, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            // Validate user type
            if (user.getType() != User.UserType.CANDIDATE) {
                throw new ServiceException("User with ID " + userId + " is not a candidate");
            }

            candidate.setUser(user);
            return candidateRepository.save(candidate);
        } catch (DataAccessException e) {
            logger.error("Error saving candidate with user ID: {}", userId, e);
            throw new ServiceException("Failed to save candidate", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!candidateRepository.existsById(id)) {
                throw new ResourceNotFoundException("Candidate", "id", id);
            }
            candidateRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error deleting candidate with ID: {}", id, e);
            throw new ServiceException("Failed to delete candidate with ID: " + id, e);
        }
    }
}