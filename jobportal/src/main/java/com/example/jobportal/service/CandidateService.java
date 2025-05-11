package com.example.jobportal.service;

import com.example.jobportal.model.Candidate;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.CandidateRepository;
import com.example.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, UserRepository userRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Transactional
    public Candidate save(Candidate candidate, Long userId) {
        if (candidate.getCandidateId() == null) {
            // Creating a new candidate
            if (userId == null) {
                throw new IllegalArgumentException("User ID must be provided for new candidates");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            candidate.setUser(user);
        } else {
            // Updating an existing candidate
            Candidate existingCandidate = candidateRepository.findById(candidate.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            // Keep the existing user relationship
            candidate.setUser(existingCandidate.getUser());

            // If userId is provided, use it (this allows changing the associated user)
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                candidate.setUser(user);
            }
        }

        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate update(Candidate candidate) {
        // For explicit updates without user change
        if (candidate.getCandidateId() == null) {
            throw new IllegalArgumentException("Candidate ID must be provided for updates");
        }

        Candidate existingCandidate = candidateRepository.findById(candidate.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Keep the existing user relationship
        candidate.setUser(existingCandidate.getUser());

        return candidateRepository.save(candidate);
    }

    public void deleteById(Long id) {
        candidateRepository.deleteById(id);
    }
}