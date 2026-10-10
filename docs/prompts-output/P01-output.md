## P01 output — Conceptual model → domain classes 

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
    AVAILABLE, RESERVED, ISSUED, DISCARDED, EXPIRED
}
```

```java
// IssueOutcome.java
public enum IssueOutcome {
    TRANSFUSED, RETURNED, WASTED
}
```

## Domain Classes

```java
// Donor.java
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Donor {
    private String fiscalCode;
    private String name;
    private LocalDate dob;
    private BloodType bloodType;
    private List<Donation> donations;

    public Donor(String fiscalCode, String name, LocalDate dob, BloodType bloodType) {
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.dob = dob;
        this.bloodType = bloodType;
        this.donations = new ArrayList<>();
    }

    public String getFiscalCode() { return fiscalCode; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public BloodType getBloodType() { return bloodType; }

    public List<Donation> getDonations() {
        return Collections.unmodifiableList(donations);
    }

    // Package-private: called only by Donation constructor
    void addDonation(Donation donation) {
        this.donations.add(donation);
    }
}
```

```java
// Donation.java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Donation {
    private LocalDateTime collectedAt;
    private String collectionSite;
    private double volume;
    private ScreeningStatus screeningStatus;
    private Donor donor;
    private List<BloodPack> bloodPacks;

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

    // Package-private: called only by BloodPack constructor
    void addBloodPack(BloodPack bloodPack) {
        this.bloodPacks.add(bloodPack);
    }
}
```

```java
// BloodPack.java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloodPack {
    private String packCode;
    private LocalDateTime producedAt;
    private LocalDateTime expiresAt;
    private BloodPackState state;
    private boolean redCells;
    private boolean plasma;
    private boolean platelets;
    private Donation donation;
    private StorageLocation storageLocation;
    private List<Issue> issues;

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
        this.storageLocation = null;
        this.issues = new ArrayList<>();
        if (donation != null) {
            donation.addBloodPack(this);
        }
    }

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

    // Package-private: called only by Issue constructor
    void addIssue(Issue issue) {
        this.issues.add(issue);
    }
}
```

```java
// StorageLocation.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageLocation {
    private String code;
    private String tempRange;
    private List<BloodPack> bloodPacks;

    public StorageLocation(String code, String tempRange) {
        this.code = code;
        this.tempRange = tempRange;
        this.bloodPacks = new ArrayList<>();
    }

    public String getCode() { return code; }
    public String getTempRange() { return tempRange; }

    public List<BloodPack> getBloodPacks() {
        return Collections.unmodifiableList(bloodPacks);
    }

    // Package-private: called only by BloodPack.setStorageLocation
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Patient {
    private String healthNum;
    private String name;
    private LocalDate dob;
    private BloodType bloodType;
    private List<Issue> issues;

    public Patient(String healthNum, String name, LocalDate dob, BloodType bloodType) {
        this.healthNum = healthNum;
        this.name = name;
        this.dob = dob;
        this.bloodType = bloodType;
        this.issues = new ArrayList<>();
    }

    public String getHealthNum() { return healthNum; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public BloodType getBloodType() { return bloodType; }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    // Package-private: called only by Issue constructor
    void addIssue(Issue issue) {
        this.issues.add(issue);
    }
}
```

```java
// Issue.java
import java.time.LocalDateTime;

public class Issue {
    private LocalDateTime issuedAt;
    private String ward;
    private boolean emergencyRelease;
    private IssueOutcome outcome;
    private Patient patient;
    private BloodPack bloodPack;

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