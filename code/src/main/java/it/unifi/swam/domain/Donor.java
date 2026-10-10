package it.unifi.swam.domain;

// Donor.java
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
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String fiscalCode;

    private String name;
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @OneToMany(mappedBy = "donor")
    private List<Donation> donations;

    protected Donor() {
        this.donations = new ArrayList<>();
    }

    public Donor(String fiscalCode, String name, LocalDate dob, BloodType bloodType) {
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.dob = dob;
        this.bloodType = bloodType;
        this.donations = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getFiscalCode() { return fiscalCode; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public BloodType getBloodType() { return bloodType; }

    public List<Donation> getDonations() {
        return Collections.unmodifiableList(donations);
    }

    void addDonation(Donation donation) {
        this.donations.add(donation);
    }
}