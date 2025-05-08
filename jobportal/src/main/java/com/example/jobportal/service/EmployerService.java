package com.example.jobportal.service;

import com.example.jobportal.model.Employer;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.EmployerRepository;
import com.example.jobportal.repository.UserRepository;
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
        employer.setUser(user);
        return employerRepository.save(employer);
    }

    public void deleteById(Long id) {
        employerRepository.deleteById(id);
    }
}