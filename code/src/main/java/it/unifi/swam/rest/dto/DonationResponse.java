package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.BloodPack;
import it.unifi.swam.domain.Donation;
import it.unifi.swam.domain.ScreeningStatus;

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
