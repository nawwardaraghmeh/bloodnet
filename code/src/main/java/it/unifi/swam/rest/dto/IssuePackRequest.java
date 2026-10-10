package it.unifi.swam.rest.dto;

// IssuePackRequest.java
public record IssuePackRequest(
        Long packId,
        Long patientId,
        String ward,
        boolean emergencyRelease) {
}