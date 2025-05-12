package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"candidate", "skill"})
@Entity
@Table(name = "candidate_skills", indexes = {
        @Index(name = "idx_candidate_skill", columnList = "candidate_id, skill_id")
})
public class CandidateSkill {
    @EmbeddedId
    private CandidateSkillId id = new CandidateSkillId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    @JsonIgnore
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private Integer proficiency; // 1-5

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class CandidateSkillId implements Serializable {
        @Column(name = "candidate_id")
        private Long candidateId;

        @Column(name = "skill_id")
        private Long skillId;

        public CandidateSkillId(Long candidateId, Long skillId) {
            this.candidateId = candidateId;
            this.skillId = skillId;
        }
    }

    @Override
    public String toString() {
        return "CandidateSkill{" +
                "skillName=" + (skill != null ? skill.getName() : "null") +
                ", proficiency=" + proficiency +
                '}';
    }
}