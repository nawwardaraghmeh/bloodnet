package it.unifi.swam.domain;

// StorageLocation.java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class StorageLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private double minTempC;
    private double maxTempC;

    @OneToMany(mappedBy = "storageLocation")
    private List<BloodPack> bloodPacks;

    protected StorageLocation() {
        this.bloodPacks = new ArrayList<>();
    }

    public StorageLocation(String code, double minTempC, double maxTempC) {
        this.code = code;
        this.minTempC = minTempC;
        this.maxTempC = maxTempC;
        this.bloodPacks = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public double getMinTempC() { return minTempC; }
    public double getMaxTempC() { return maxTempC; }

    public List<BloodPack> getBloodPacks() {
        return Collections.unmodifiableList(bloodPacks);
    }

    void addBloodPack(BloodPack bloodPack) {
        this.bloodPacks.add(bloodPack);
    }

    void removeBloodPack(BloodPack bloodPack) {
        this.bloodPacks.remove(bloodPack);
    }
}