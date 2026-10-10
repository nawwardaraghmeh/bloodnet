package it.unifi.swam.services;

import it.unifi.swam.dao.*;
import it.unifi.swam.domain.*;

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