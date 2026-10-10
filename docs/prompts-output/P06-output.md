## P06 — Services → REST controllers

## Modified service

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
                                   Long donorId, List<BloodPackSpec> packs) {
        Donor donor = donorDao.find(donorId);
        if (donor == null) {
            throw new IllegalArgumentException("Donor not found");
        }
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

## Request DTOs

```java
// RegisterDonorRequest.java
import java.time.LocalDate;

public record RegisterDonorRequest(
        String fiscalCode,
        String name,
        LocalDate dob,
        BloodType bloodType) {
}
```

```java
// BloodPackRequest.java
import java.time.LocalDateTime;

public record BloodPackRequest(
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        boolean redCells,
        boolean plasma,
        boolean platelets) {
}
```

```java
// RecordDonationRequest.java
import java.time.LocalDateTime;
import java.util.List;

public record RecordDonationRequest(
        LocalDateTime collectedAt,
        String collectionSite,
        double volume,
        Long donorId,
        List<BloodPackRequest> packs) {
}
```

```java
// RecordScreeningResultRequest.java
public record RecordScreeningResultRequest(
        ScreeningStatus status) {
}
```

```java
// RegisterStorageLocationRequest.java
public record RegisterStorageLocationRequest(
        String code,
        double minTempC,
        double maxTempC) {
}
```

```java
// RegisterPatientRequest.java
import java.time.LocalDate;

public record RegisterPatientRequest(
        String healthNum,
        String name,
        LocalDate dob,
        BloodType bloodType) {
}
```

```java
// IssuePackRequest.java
public record IssuePackRequest(
        Long packId,
        Long patientId,
        String ward,
        boolean emergencyRelease) {
}
```

```java
// RecordIssueOutcomeRequest.java
public record RecordIssueOutcomeRequest(
        IssueOutcome outcome) {
}
```

## Response DTOs

```java
// DonorResponse.java
import java.time.LocalDate;

public record DonorResponse(
        Long id,
        String fiscalCode,
        String name,
        LocalDate dob,
        BloodType bloodType) {

    public static DonorResponse from(Donor donor) {
        return new DonorResponse(donor.getId(), donor.getFiscalCode(),
                donor.getName(), donor.getDob(), donor.getBloodType());
    }
}
```

```java
// DonationResponse.java
import java.time.LocalDateTime;
import java.util.List;

public record DonationResponse(
        Long id,
        LocalDateTime collectedAt,
        String collectionSite,
        double volume,
        ScreeningStatus screeningStatus,
        Long donorId,
        List<String> packCodes) {

    public static DonationResponse from(Donation donation) {
        List<String> codes = donation.getBloodPacks().stream()
                .map(BloodPack::getPackCode)
                .toList();
        return new DonationResponse(donation.getId(), donation.getCollectedAt(),
                donation.getCollectionSite(), donation.getVolume(),
                donation.getScreeningStatus(), donation.getDonor().getId(), codes);
    }
}
```

```java
// BloodPackResponse.java
import java.time.LocalDateTime;

public record BloodPackResponse(
        Long id,
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        BloodPackState state,
        boolean redCells,
        boolean plasma,
        boolean platelets,
        Long donationId,
        Long storageLocationId) {

    public static BloodPackResponse from(BloodPack pack) {
        Long locationId = pack.getStorageLocation() == null
                ? null
                : pack.getStorageLocation().getId();
        return new BloodPackResponse(pack.getId(), pack.getPackCode(),
                pack.getProducedAt(), pack.getExpiresAt(), pack.getState(),
                pack.isRedCells(), pack.isPlasma(), pack.isPlatelets(),
                pack.getDonation().getId(), locationId);
    }
}
```

```java
// StorageLocationResponse.java
public record StorageLocationResponse(
        Long id,
        String code,
        double minTempC,
        double maxTempC) {

    public static StorageLocationResponse from(StorageLocation location) {
        return new StorageLocationResponse(location.getId(), location.getCode(),
                location.getMinTempC(), location.getMaxTempC());
    }
}
```

```java
// PatientResponse.java
import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String healthNum,
        String name,
        LocalDate dob,
        BloodType bloodType) {

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getHealthNum(),
                patient.getName(), patient.getDob(), patient.getBloodType());
    }
}
```

```java
// IssueResponse.java
import java.time.LocalDateTime;

public record IssueResponse(
        Long id,
        LocalDateTime issuedAt,
        String ward,
        boolean emergencyRelease,
        IssueOutcome outcome,
        Long patientId,
        Long bloodPackId) {

    public static IssueResponse from(Issue issue) {
        return new IssueResponse(issue.getId(), issue.getIssuedAt(), issue.getWard(),
                issue.isEmergencyRelease(), issue.getOutcome(),
                issue.getPatient().getId(), issue.getBloodPack().getId());
    }
}
```

## Controllers

```java
// DonationController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/collection")
@Produces(MediaType.APPLICATION_JSON)
public class DonationController {

    @Inject
    DonationService donationService;

    @POST
    @Path("/donors")
    @Consumes(MediaType.APPLICATION_JSON)
    public DonorResponse registerDonor(RegisterDonorRequest request) {
        Donor donor = donationService.registerDonor(
                request.fiscalCode(), request.name(), request.dob(), request.bloodType());
        return DonorResponse.from(donor);
    }

    @POST
    @Path("/donations")
    @Consumes(MediaType.APPLICATION_JSON)
    public DonationResponse recordDonation(RecordDonationRequest request) {
        List<BloodPackSpec> specs = request.packs().stream()
                .map(p -> new BloodPackSpec(p.packCode(), p.producedAt(), p.expiresAt(),
                        p.redCells(), p.plasma(), p.platelets()))
                .toList();
        Donation donation = donationService.recordDonation(
                request.collectedAt(), request.collectionSite(), request.volume(),
                request.donorId(), specs);
        return DonationResponse.from(donation);
    }

    @POST
    @Path("/donations/{donationId}/screening")
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordScreeningResult(@PathParam("donationId") Long donationId,
                                      RecordScreeningResultRequest request) {
        donationService.recordScreeningResult(donationId, request.status());
    }
}
```

```java
// InventoryController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryController {

    @Inject
    InventoryService inventoryService;

    @POST
    @Path("/storage-locations")
    @Consumes(MediaType.APPLICATION_JSON)
    public StorageLocationResponse registerStorageLocation(RegisterStorageLocationRequest request) {
        StorageLocation location = inventoryService.registerStorageLocation(
                request.code(), request.minTempC(), request.maxTempC());
        return StorageLocationResponse.from(location);
    }

    @POST
    @Path("/blood-packs/{packId}/storage-locations/{locationId}")
    public void placePackInStorage(@PathParam("packId") Long packId,
                                   @PathParam("locationId") Long locationId) {
        inventoryService.placePackInStorage(packId, locationId);
    }

    @GET
    @Path("/storage-locations/{locationId}/blood-packs")
    public List<BloodPackResponse> listPacksInLocation(@PathParam("locationId") Long locationId) {
        return inventoryService.listPacksInLocation(locationId).stream()
                .map(BloodPackResponse::from)
                .toList();
    }
}
```

```java
// TransfusionController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/transfusion")
@Produces(MediaType.APPLICATION_JSON)
public class TransfusionController {

    @Inject
    TransfusionService transfusionService;

    @POST
    @Path("/patients")
    @Consumes(MediaType.APPLICATION_JSON)
    public PatientResponse registerPatient(RegisterPatientRequest request) {
        Patient patient = transfusionService.registerPatient(
                request.healthNum(), request.name(), request.dob(), request.bloodType());
        return PatientResponse.from(patient);
    }

    @POST
    @Path("/issues")
    @Consumes(MediaType.APPLICATION_JSON)
    public IssueResponse issuePackToPatient(IssuePackRequest request) {
        Issue issue = transfusionService.issuePackToPatient(
                request.packId(), request.patientId(), request.ward(),
                request.emergencyRelease());
        return IssueResponse.from(issue);
    }

    @POST
    @Path("/issues/{issueId}/outcome")
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordIssueOutcome(@PathParam("issueId") Long issueId,
                                   RecordIssueOutcomeRequest request) {
        transfusionService.recordIssueOutcome(issueId, request.outcome());
    }
}
```