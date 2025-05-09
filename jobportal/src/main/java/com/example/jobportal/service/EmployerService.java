package com.example.jobportal.service;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.model.Employer;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.EmployerRepository;
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
public class EmployerService {

    private static final Logger logger = LoggerFactory.getLogger(EmployerService.class);

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployerService(EmployerRepository employerRepository, UserRepository userRepository) {
        this.employerRepository = employerRepository;
        this.userRepository = userRepository;
    }

    public List<Employer> findAll() {
        try {
            return employerRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all employers", e);
            throw new ServiceException("Failed to retrieve all employers", e);
        }
    }

    public Optional<Employer> findById(Long id) {
        try {
            return employerRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching employer with ID: {}", id, e);
            throw new ServiceException("Failed to retrieve employer with ID: " + id, e);
        }
    }

    public Employer getById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));
    }

    @Transactional
    public Employer save(Employer employer, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            // Validate user type
            if (user.getType() != User.UserType.EMPLOYER) {
                throw new ServiceException("User with ID " + userId + " is not an employer");
            }

            employer.setUser(user);
            return employerRepository.save(employer);
        } catch (DataAccessException e) {
            logger.error("Error saving employer with user ID: {}", userId, e);
            throw new ServiceException("Failed to save employer", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!employerRepository.existsById(id)) {
                throw new ResourceNotFoundException("Employer", "id", id);
            }
            employerRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error deleting employer with ID: {}", id, e);
            throw new ServiceException("Failed to delete employer with ID: " + id, e);
        }
    }
}