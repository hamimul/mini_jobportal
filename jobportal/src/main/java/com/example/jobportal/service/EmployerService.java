package com.example.jobportal.service;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.EmployerRepository;
import com.example.jobportal.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployerService(EmployerRepository employerRepository, UserRepository userRepository) {
        this.employerRepository = employerRepository;
        this.userRepository = userRepository;
    }

    public List<Employer> findAll() {
        return employerRepository.findAll();
    }

    public Optional<Employer> findById(Long id) {
        return employerRepository.findById(id);
    }

    @Transactional
    public Employer save(Employer employer, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // This is the key part - set the ID before saving
        employer.setEmployerId(userId);  // Set the ID to match the User ID
        employer.setUser(user);

        return employerRepository.save(employer);
    }

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public Employer update(Employer updatedEmployer) {
        // Get the current state from the database
        Employer currentEmployer = employerRepository.findById(updatedEmployer.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Detach the current entity from the persistence context
        entityManager.detach(currentEmployer);

        // Update fields on the current employer
        currentEmployer.setCompanyName(updatedEmployer.getCompanyName());
        currentEmployer.setIndustry(updatedEmployer.getIndustry());

        // Merge the updated entity back
        return entityManager.merge(currentEmployer);
    }

    public void deleteById(Long id) {
        employerRepository.deleteById(id);
    }
}