## P10 — Read endpoint

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

    public BloodPack viewBloodPack(Long packId) {
        BloodPack pack = bloodPackDao.find(packId);
        if (pack == null) {
            throw new IllegalArgumentException("Blood pack not found");
        }
        return pack;
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

    @GET
    @Path("/blood-packs/{packId}")
    public BloodPackResponse viewBloodPack(@PathParam("packId") Long packId) {
        return BloodPackResponse.from(inventoryService.viewBloodPack(packId));
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
        List<Long> packIds,
        List<String> packCodes) {

    public static DonationResponse from(Donation donation) {
        List<Long> ids = donation.getBloodPacks().stream()
                .map(BloodPack::getId)
                .toList();
        List<String> codes = donation.getBloodPacks().stream()
                .map(BloodPack::getPackCode)
                .toList();
        return new DonationResponse(donation.getId(), donation.getCollectedAt(),
                donation.getCollectionSite(), donation.getVolume(),
                donation.getScreeningStatus(), donation.getDonor().getId(),
                ids, codes);
    }
}
```