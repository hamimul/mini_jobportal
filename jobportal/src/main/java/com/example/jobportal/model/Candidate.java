package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidate_location", columnList = "location")
})
public class Candidate {
    @Id
    private Long candidateId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String location;
    private Integer yearsExperience;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CandidateSkill> skills = new HashSet<>();
}