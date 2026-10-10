package it.unifi.swam.rest.dto;

// RegisterStorageLocationRequest.java
public record RegisterStorageLocationRequest(
        String code,
        double minTempC,
        double maxTempC) {
}