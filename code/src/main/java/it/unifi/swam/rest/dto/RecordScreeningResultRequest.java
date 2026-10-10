package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.ScreeningStatus;

// RecordScreeningResultRequest.java
public record RecordScreeningResultRequest(
        ScreeningStatus status) {
}