package it.unifi.swam.domain;

// BloodPack.java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class BloodPack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String packCode;

    private LocalDateTime producedAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private BloodPackState state;

    private boolean redCells;
    private boolean plasma;
    private boolean platelets;

    @ManyToOne(optional = false)
    private Donation donation;

    @ManyToOne
    private StorageLocation storageLocation;

    @OneToMany(mappedBy = "bloodPack")
    private List<Issue> issues;

    protected BloodPack() {
        this.issues = new ArrayList<>();
    }

    public BloodPack(String packCode, LocalDateTime producedAt, LocalDateTime expiresAt,
                     BloodPackState state, boolean redCells, boolean plasma,
                     boolean platelets, Donation donation) {
        this.packCode = packCode;
        this.producedAt = producedAt;
        this.expiresAt = expiresAt;
        this.state = state;
        this.redCells = redCells;
        this.plasma = plasma;
        this.platelets = platelets;
        this.donation = donation;
        this.issues = new ArrayList<>();
        if (donation != null) {
            donation.addBloodPack(this);
        }
    }

    public Long getId() { return id; }
    public String getPackCode() { return packCode; }
    public LocalDateTime getProducedAt() { return producedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public BloodPackState getState() { return state; }
    public boolean isRedCells() { return redCells; }
    public boolean isPlasma() { return plasma; }
    public boolean isPlatelets() { return platelets; }
    public Donation getDonation() { return donation; }
    public StorageLocation getStorageLocation() { return storageLocation; }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isCompatibleWith(Patient patient) {
        if (patient == null || donation == null || donation.getDonor() == null) {
            return false;
        }
        return donation.getDonor().getBloodType().canDonateTo(patient.getBloodType());
    }

    public boolean canBeIssued() {
        return state == BloodPackState.AVAILABLE && !isExpired();
    }

    public void markAvailable() {
        if (state != BloodPackState.QUARANTINED) {
            throw new IllegalStateException("Only quarantined packs can become available");
        }
        this.state = BloodPackState.AVAILABLE;
    }

    public void discard() {
        if (state != BloodPackState.QUARANTINED
                && state != BloodPackState.AVAILABLE
                && state != BloodPackState.ISSUED) {
            throw new IllegalStateException("Pack cannot be discarded from state " + state);
        }
        this.state = BloodPackState.DISCARDED;
    }

    public void issue() {
        if (state != BloodPackState.AVAILABLE) {
            throw new IllegalStateException("Only available packs can be issued");
        }
        if (isExpired()) {
            throw new IllegalStateException("Expired pack cannot be issued");
        }
        this.state = BloodPackState.ISSUED;
    }

    public void returnToAvailable() {
        if (state != BloodPackState.ISSUED) {
            throw new IllegalStateException("Only issued packs can be returned");
        }
        this.state = BloodPackState.AVAILABLE;
    }

    public void markTransfused() {
        if (state != BloodPackState.ISSUED) {
            throw new IllegalStateException("Only issued packs can be transfused");
        }
        this.state = BloodPackState.TRANSFUSED;
    }

    public void setStorageLocation(StorageLocation newLocation) {
        if (this.storageLocation == newLocation) {
            return;
        }
        if (this.storageLocation != null) {
            this.storageLocation.removeBloodPack(this);
        }
        this.storageLocation = newLocation;
        if (newLocation != null) {
            newLocation.addBloodPack(this);
        }
    }

    void addIssue(Issue issue) {
        this.issues.add(issue);
    }
}