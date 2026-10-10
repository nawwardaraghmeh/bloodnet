package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

// StorageLocationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageLocationDao extends BaseDao<StorageLocation> {

    public StorageLocationDao() {
        super(StorageLocation.class);
    }
}