package com.example.jobportal.repository;

import com.example.jobportal.model.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkill.CandidateSkillId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CandidateSkill cs WHERE cs.candidate.candidateId = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") Long candidateId);
}