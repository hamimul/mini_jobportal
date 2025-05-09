package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "candidate_skills", indexes = {
        @Index(name = "idx_candidate_skill", columnList = "candidate_id, skill_id")
})
public class CandidateSkill {
    @EmbeddedId
    private CandidateSkillId id = new CandidateSkillId();

    @ManyToOne
    @MapsId("candidateId")
    private Candidate candidate;

    @ManyToOne
    @MapsId("skillId")
    private Skill skill;

    private Integer proficiency; // 1-5

    @Embeddable
    @Data
    public static class CandidateSkillId implements java.io.Serializable {
        private Long candidateId;  // Changed from userId
        private Long skillId;
    }
}