package it.unifi.swam.domain;

// Donation.java
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
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime collectedAt;
    private String collectionSite;
    private double volume;

    @Enumerated(EnumType.STRING)
    private ScreeningStatus screeningStatus;

    @ManyToOne(optional = false)
    private Donor donor;

    @OneToMany(mappedBy = "donation")
    private List<BloodPack> bloodPacks;

    protected Donation() {
        this.bloodPacks = new ArrayList<>();
    }

    public Donation(LocalDateTime collectedAt, String collectionSite, double volume,
                    ScreeningStatus screeningStatus, Donor donor) {
        this.collectedAt = collectedAt;
        this.collectionSite = collectionSite;
        this.volume = volume;
        this.screeningStatus = screeningStatus;
        this.donor = donor;
        this.bloodPacks = new ArrayList<>();
        if (donor != null) {
            donor.addDonation(this);
        }
    }

    public Long getId() { return id; }
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public String getCollectionSite() { return collectionSite; }
    public double getVolume() { return volume; }
    public ScreeningStatus getScreeningStatus() { return screeningStatus; }
    public Donor getDonor() { return donor; }

    public List<BloodPack> getBloodPacks() {
        return Collections.unmodifiableList(bloodPacks);
    }

    public void setScreeningStatus(ScreeningStatus screeningStatus) {
        this.screeningStatus = screeningStatus;
    }

    void addBloodPack(BloodPack bloodPack) {
        this.bloodPacks.add(bloodPack);
    }
}