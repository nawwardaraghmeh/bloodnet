## P03 & P03b — Domain classes → JPA-annotated entities

## Enums

```java
// BloodType.java
public enum BloodType {
    A_POSITIVE, A_NEGATIVE,
    B_POSITIVE, B_NEGATIVE,
    AB_POSITIVE, AB_NEGATIVE,
    O_POSITIVE, O_NEGATIVE
}
```

```java
// ScreeningStatus.java
public enum ScreeningStatus {
    PENDING, PASSED, FAILED
}
```

```java
// BloodPackState.java
public enum BloodPackState {
    QUARANTINED, AVAILABLE, RESERVED, ISSUED, DISCARDED
}
```

```java
// IssueOutcome.java
public enum IssueOutcome {
    TRANSFUSED, RETURNED, WASTED
}
```

## Entities

```java
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
```

```java
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
```

```java
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

    public void setState(BloodPackState state) {
        this.state = state;
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
```

```java
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
```

```java
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
```

```java
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
```