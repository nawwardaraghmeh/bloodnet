package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

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