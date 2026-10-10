package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.BloodPack;
import it.unifi.swam.domain.BloodPackState;

// BloodPackResponse.java
import java.time.LocalDateTime;

public record BloodPackResponse(
        Long id,
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        BloodPackState state,
        boolean redCells,
        boolean plasma,
        boolean platelets,
        Long donationId,
        Long storageLocationId) {

    public static BloodPackResponse from(BloodPack pack) {
        Long locationId = pack.getStorageLocation() == null
                ? null
                : pack.getStorageLocation().getId();
        return new BloodPackResponse(pack.getId(), pack.getPackCode(),
                pack.getProducedAt(), pack.getExpiresAt(), pack.getState(),
                pack.isRedCells(), pack.isPlasma(), pack.isPlatelets(),
                pack.getDonation().getId(), locationId);
    }
}