package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "skills", indexes = {
        @Index(name = "idx_skill_name", columnList = "name", unique = true)
})
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "skill")
    private Set<CandidateSkill> candidates = new HashSet<>();

    @OneToMany(mappedBy = "skill")
    private Set<JobSkill> jobs = new HashSet<>();
}