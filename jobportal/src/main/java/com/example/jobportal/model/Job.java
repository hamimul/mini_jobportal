package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_employer", columnList = "employer_id"),
        @Index(name = "idx_job_location", columnList = "location"),
        @Index(name = "idx_job_created_at", columnList = "created_at")
})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    private String title;
    private String location;
    private Integer minExperience;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<JobSkill> requiredSkills = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructor
    public Job() {
        this.requiredSkills = new HashSet<>();
    }
}