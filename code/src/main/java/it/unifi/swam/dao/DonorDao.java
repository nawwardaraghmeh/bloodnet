package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

// DonorDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonorDao extends BaseDao<Donor> {

    public DonorDao() {
        super(Donor.class);
    }
}