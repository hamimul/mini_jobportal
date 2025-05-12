package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"job", "skill"})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobSkill)) return false;
        JobSkill jobSkill = (JobSkill) o;
        return id != null && id.equals(jobSkill.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Embeddable
    @Getter
    @Setter
    public static class JobSkillId implements java.io.Serializable {
        private Long jobId;
        private Long skillId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JobSkillId)) return false;
            JobSkillId that = (JobSkillId) o;
            return jobId != null && skillId != null 
                   && jobId.equals(that.jobId) 
                   && skillId.equals(that.skillId);
        }

        @Override
        public int hashCode() {
            return jobId != null && skillId != null ? 
                   31 * jobId.hashCode() + skillId.hashCode() : 
                   super.hashCode();
        }
    }
}