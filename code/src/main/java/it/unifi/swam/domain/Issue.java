package it.unifi.swam.domain;

// Issue.java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime issuedAt;
    private String ward;
    private boolean emergencyRelease;

    @Enumerated(EnumType.STRING)
    private IssueOutcome outcome;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private BloodPack bloodPack;

    protected Issue() {
    }

    public Issue(LocalDateTime issuedAt, String ward, boolean emergencyRelease,
                 Patient patient, BloodPack bloodPack) {
        this.issuedAt = issuedAt;
        this.ward = ward;
        this.emergencyRelease = emergencyRelease;
        this.outcome = null;
        this.patient = patient;
        this.bloodPack = bloodPack;
        if (patient != null) {
            patient.addIssue(this);
        }
        if (bloodPack != null) {
            bloodPack.addIssue(this);
        }
    }

    public Long getId() { return id; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public String getWard() { return ward; }
    public boolean isEmergencyRelease() { return emergencyRelease; }
    public IssueOutcome getOutcome() { return outcome; }
    public Patient getPatient() { return patient; }
    public BloodPack getBloodPack() { return bloodPack; }

    public void setOutcome(IssueOutcome outcome) {
        this.outcome = outcome;
    }
}