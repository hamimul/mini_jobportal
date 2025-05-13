package com.example.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobMatchDTO {
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Integer minExperience;
    private Double matchScore;
    private Integer rank;
}