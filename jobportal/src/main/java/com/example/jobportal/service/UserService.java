package com.example.jobportal.service;

import com.example.jobportal.exception.ResourceNotFoundException;
import com.example.jobportal.exception.ServiceException;
import com.example.jobportal.model.User;
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
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all users", e);
            throw new ServiceException("Failed to retrieve all users", e);
        }
    }

    public Optional<User> findById(Long id) {
        try {
            return userRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching user with ID: {}", id, e);
            throw new ServiceException("Failed to retrieve user with ID: " + id, e);
        }
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new ResourceNotFoundException("User", "email", email);
            }
            return user;
        } catch (DataAccessException e) {
            logger.error("Error fetching user with email: {}", email, e);
            throw new ServiceException("Failed to retrieve user with email: " + email, e);
        }
    }

    @Transactional
    public User save(User user) {
        try {
            // Check if email already exists for a new user
            if (user.getId() == null && userRepository.findByEmail(user.getEmail()) != null) {
                throw new ServiceException("Email already in use: " + user.getEmail());
            }
            return userRepository.save(user);
        } catch (DataAccessException e) {
            logger.error("Error saving user: {}", user, e);
            throw new ServiceException("Failed to save user", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new ResourceNotFoundException("User", "id", id);
            }
            userRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error deleting user with ID: {}", id, e);
            throw new ServiceException("Failed to delete user with ID: " + id, e);
        }
    }
}