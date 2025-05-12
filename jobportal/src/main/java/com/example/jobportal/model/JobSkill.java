package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@ToString(exclude = {"job", "skill"})
@EqualsAndHashCode(exclude = {"job", "skill"})
@Entity
@Table(name = "job_skills", indexes = {
        @Index(name = "idx_job_skill", columnList = "job_id, skill_id")
})
public class JobSkill {
    @EmbeddedId
    private JobSkillId id = new JobSkillId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobId")
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private Integer importance; // 1-5

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class JobSkillId implements java.io.Serializable {
        @Column(name = "job_id")
        private Long jobId;

        @Column(name = "skill_id")
        private Long skillId;

        // Default constructor
        public JobSkillId() {}

        // Constructor with parameters
        public JobSkillId(Long jobId, Long skillId) {
            this.jobId = jobId;
            this.skillId = skillId;
        }
    }
}