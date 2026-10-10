package it.unifi.swam.services;

import it.unifi.swam.dao.*;
import it.unifi.swam.domain.*;

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