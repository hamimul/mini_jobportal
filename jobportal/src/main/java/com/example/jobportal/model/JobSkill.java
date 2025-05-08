package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "job_skills", indexes = {
        @Index(name = "idx_job_skill", columnList = "job_id, skill_id")
})
public class JobSkill {
    @EmbeddedId
    private JobSkillId id = new JobSkillId();

    @ManyToOne
    @MapsId("jobId")
    private Job job;

    @ManyToOne
    @MapsId("skillId")
    private Skill skill;

    private Integer importance; // 1-5

    @Embeddable
    @Data
    public static class JobSkillId implements java.io.Serializable {
        private Long jobId;
        private Long skillId;
    }
}