package it.unifi.swam.services;

import it.unifi.swam.dao.*;
import it.unifi.swam.domain.*;

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