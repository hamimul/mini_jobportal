package com.example.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "employers", indexes = {
        @Index(name = "idx_employer_company", columnList = "company_name")
})
public class Employer {
    @Id
    private Long employerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;
    private String industry;

    @Version
    private Long version; // Add this for optimistic locking

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();
}