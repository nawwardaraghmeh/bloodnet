package it.unifi.swam.domain;

// Patient.java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String healthNum;

    private String name;
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @OneToMany(mappedBy = "patient")
    private List<Issue> issues;

    protected Patient() {
        this.issues = new ArrayList<>();
    }

    public Patient(String healthNum, String name, LocalDate dob, BloodType bloodType) {
        this.healthNum = healthNum;
        this.name = name;
        this.dob = dob;
        this.bloodType = bloodType;
        this.issues = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getHealthNum() { return healthNum; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public BloodType getBloodType() { return bloodType; }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    void addIssue(Issue issue) {
        this.issues.add(issue);
    }
}