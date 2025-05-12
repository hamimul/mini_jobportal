package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidate_location", columnList = "location")
})
public class Candidate {
    @Id
    private Long candidateId;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "location")
    private String location;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CandidateSkill> skills = new HashSet<>();

    @Version
    private Long version;

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", location='" + location + '\'' +
                ", yearsExperience=" + yearsExperience +
                '}';
    }

    // Helper methods
    public void addSkill(CandidateSkill skill) {
        skills.add(skill);
        skill.setCandidate(this);
    }

    public void removeSkill(CandidateSkill skill) {
        skills.remove(skill);
        skill.setCandidate(null);
    }

    public void clearSkills() {
        skills.forEach(skill -> skill.setCandidate(null));
        skills.clear();
    }
}