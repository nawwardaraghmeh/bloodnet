package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

// DonationDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DonationDao extends BaseDao<Donation> {

    public DonationDao() {
        super(Donation.class);
    }
}