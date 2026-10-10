package it.unifi.swam.dao;

import it.unifi.swam.domain.*;

// PatientDao.java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientDao extends BaseDao<Patient> {

    public PatientDao() {
        super(Patient.class);
    }
}