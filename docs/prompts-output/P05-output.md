## P05 — Use cases and statechart → service layer

## Enums

```java
// BloodType.java
public enum BloodType {
    A_POSITIVE, A_NEGATIVE,
    B_POSITIVE, B_NEGATIVE,
    AB_POSITIVE, AB_NEGATIVE,
    O_POSITIVE, O_NEGATIVE;

    public boolean canDonateTo(BloodType recipient) {
        if (recipient == null) {
            return false;
        }
        switch (this) {
            case O_NEGATIVE:
                return true;
            case O_POSITIVE:
                return recipient == O_POSITIVE || recipient == A_POSITIVE
                        || recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case A_NEGATIVE:
                return recipient == A_NEGATIVE || recipient == A_POSITIVE
                        || recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case A_POSITIVE:
                return recipient == A_POSITIVE || recipient == AB_POSITIVE;
            case B_NEGATIVE:
                return recipient == B_NEGATIVE || recipient == B_POSITIVE
                        || recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case B_POSITIVE:
                return recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case AB_NEGATIVE:
                return recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case AB_POSITIVE:
                return recipient == AB_POSITIVE;
            default:
                return false;
        }
    }
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
    QUARANTINED, AVAILABLE, ISSUED, TRANSFUSED, DISCARDED
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

## DAOs

```java
// BaseDao.java
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

public abstract class BaseDao<T> {

    @Inject
    protected EntityManager em;

    private final Class<T> entityClass;

    protected BaseDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T find(Long id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }

    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    public T update(T entity) {
        return em.merge(entity);
    }

    public void delete(T entity) {
        T managed = em.contains(entity) ? entity : em.merge(entity);
        em.remove(managed);
    }
}
```

```java
// DonorDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonorDao extends BaseDao<Donor> {

    public DonorDao() {
        super(Donor.class);
    }
}
```

```java
// DonationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonationDao extends BaseDao<Donation> {

    public DonationDao() {
        super(Donation.class);
    }
}
```

```java
// BloodPackDao.java
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BloodPackDao extends BaseDao<BloodPack> {

    public BloodPackDao() {
        super(BloodPack.class);
    }

    public List<BloodPack> findByStorageLocation(StorageLocation location) {
        return em.createQuery(
                        "SELECT b FROM BloodPack b WHERE b.storageLocation = :location",
                        BloodPack.class)
                .setParameter("location", location)
                .getResultList();
    }
}
```

```java
// StorageLocationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageLocationDao extends BaseDao<StorageLocation> {

    public StorageLocationDao() {
        super(StorageLocation.class);
    }
}
```

```java
// PatientDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientDao extends BaseDao<Patient> {

    public PatientDao() {
        super(Patient.class);
    }
}
```

```java
// IssueDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IssueDao extends BaseDao<Issue> {

    public IssueDao() {
        super(Issue.class);
    }
}
```

## Service-layer DTO

```java
// BloodPackSpec.java
import java.time.LocalDateTime;

public record BloodPackSpec(
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        boolean redCells,
        boolean plasma,
        boolean platelets) {
}
```

## Services

```java
// DonationService.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DonationService {

    @Inject
    DonorDao donorDao;

    @Inject
    DonationDao donationDao;

    @Inject
    BloodPackDao bloodPackDao;

    @Transactional
    public Donor registerDonor(String fiscalCode, String name, LocalDate dob, BloodType bloodType) {
        Donor donor = new Donor(fiscalCode, name, dob, bloodType);
        return donorDao.save(donor);
    }

    @Transactional
    public Donation recordDonation(LocalDateTime collectedAt, String collectionSite, double volume,
                                   Donor donor, List<BloodPackSpec> packs) {
        Donation donation = new Donation(collectedAt, collectionSite, volume,
                ScreeningStatus.PENDING, donor);
        donationDao.save(donation);
        for (BloodPackSpec spec : packs) {
            BloodPack pack = new BloodPack(spec.packCode(), spec.producedAt(), spec.expiresAt(),
                    BloodPackState.QUARANTINED, spec.redCells(), spec.plasma(),
                    spec.platelets(), donation);
            bloodPackDao.save(pack);
        }
        return donation;
    }

    @Transactional
    public void recordScreeningResult(Long donationId, ScreeningStatus result) {
        Donation donation = donationDao.find(donationId);
        if (donation == null) {
            throw new IllegalArgumentException("Donation not found");
        }
        if (donation.getScreeningStatus() != ScreeningStatus.PENDING) {
            throw new IllegalStateException("Screening result already recorded");
        }
        donation.setScreeningStatus(result);
        for (BloodPack pack : donation.getBloodPacks()) {
            if (result == ScreeningStatus.PASSED) {
                pack.markAvailable();
            } else {
                pack.discard();
            }
            bloodPackDao.update(pack);
        }
        donationDao.update(donation);
    }
}
```

```java
// InventoryService.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class InventoryService {

    @Inject
    StorageLocationDao storageLocationDao;

    @Inject
    BloodPackDao bloodPackDao;

    @Transactional
    public StorageLocation registerStorageLocation(String code, double minTempC, double maxTempC) {
        StorageLocation location = new StorageLocation(code, minTempC, maxTempC);
        return storageLocationDao.save(location);
    }

    @Transactional
    public void placePackInStorage(Long packId, Long locationId) {
        BloodPack pack = bloodPackDao.find(packId);
        StorageLocation location = storageLocationDao.find(locationId);
        if (pack == null || location == null) {
            throw new IllegalArgumentException("Blood pack or storage location not found");
        }
        pack.setStorageLocation(location);
        bloodPackDao.update(pack);
    }

    public List<BloodPack> listPacksInLocation(Long locationId) {
        StorageLocation location = storageLocationDao.find(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Storage location not found");
        }
        return bloodPackDao.findByStorageLocation(location);
    }
}
```

```java
// TransfusionService.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class TransfusionService {

    @Inject
    PatientDao patientDao;

    @Inject
    IssueDao issueDao;

    @Inject
    BloodPackDao bloodPackDao;

    @Transactional
    public Patient registerPatient(String healthNum, String name, LocalDate dob, BloodType bloodType) {
        Patient patient = new Patient(healthNum, name, dob, bloodType);
        return patientDao.save(patient);
    }

    @Transactional
    public Issue issuePackToPatient(Long packId, Long patientId, String ward, boolean emergencyRelease) {
        BloodPack pack = bloodPackDao.find(packId);
        Patient patient = patientDao.find(patientId);
        if (pack == null || patient == null) {
            throw new IllegalArgumentException("Blood pack or patient not found");
        }
        if (!pack.isCompatibleWith(patient)) {
            throw new IllegalStateException("Blood type incompatible");
        }
        if (!pack.canBeIssued()) {
            throw new IllegalStateException("Blood pack is not available or has expired");
        }
        pack.issue();
        Issue issue = new Issue(LocalDateTime.now(), ward, emergencyRelease, patient, pack);
        issueDao.save(issue);
        bloodPackDao.update(pack);
        return issue;
    }

    @Transactional
    public void recordIssueOutcome(Long issueId, IssueOutcome outcome) {
        Issue issue = issueDao.find(issueId);
        if (issue == null) {
            throw new IllegalArgumentException("Issue not found");
        }
        issue.setOutcome(outcome);
        BloodPack pack = issue.getBloodPack();
        switch (outcome) {
            case RETURNED:
                pack.returnToAvailable();
                break;
            case WASTED:
                pack.discard();
                break;
            case TRANSFUSED:
                pack.markTransfused();
                break;
        }
        issueDao.update(issue);
        bloodPackDao.update(pack);
    }
}
```