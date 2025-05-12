package com.example.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateMatchDTO {
    private Long candidateId;
    private String candidateName;
    private String location;
    private Integer yearsExperience;
    private Double matchScore;
    private Integer rank;
}