package it.unifi.swam.rest.dto;

// RecordDonationRequest.java
import java.time.LocalDateTime;
import java.util.List;

public record RecordDonationRequest(
        LocalDateTime collectedAt,
        String collectionSite,
        double volume,
        Long donorId,
        List<BloodPackRequest> packs) {
}