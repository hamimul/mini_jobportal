package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@ToString(exclude = {"candidate", "skill"})
@EqualsAndHashCode
@Entity
@Table(name = "candidate_skills", indexes = {
        @Index(name = "idx_candidate_skill", columnList = "candidate_id, skill_id")
})
public class CandidateSkill {
    @EmbeddedId
    private CandidateSkillId id = new CandidateSkillId();

    @ManyToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private Skill skill;

    private Integer proficiency; // 1-5

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class CandidateSkillId implements java.io.Serializable {
        @Column(name = "candidate_id")
        private Long candidateId;

        @Column(name = "skill_id")
        private Long skillId;
    }
}