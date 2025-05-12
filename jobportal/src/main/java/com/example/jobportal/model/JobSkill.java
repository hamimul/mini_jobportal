package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@ToString(exclude = {"job", "skill"})
@EqualsAndHashCode
@Entity
@Table(name = "job_skills", indexes = {
        @Index(name = "idx_job_skill", columnList = "job_id, skill_id")
})
public class JobSkill {
    @EmbeddedId
    private JobSkillId id = new JobSkillId();

    @ManyToOne
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
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
    }
}