package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.StorageLocation;

// StorageLocationResponse.java
public record StorageLocationResponse(
        Long id,
        String code,
        double minTempC,
        double maxTempC) {

    public static StorageLocationResponse from(StorageLocation location) {
        return new StorageLocationResponse(location.getId(), location.getCode(),
                location.getMinTempC(), location.getMaxTempC());
    }
}