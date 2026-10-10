package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.Issue;
import it.unifi.swam.domain.IssueOutcome;

// IssueResponse.java
import java.time.LocalDateTime;

public record IssueResponse(
        Long id,
        LocalDateTime issuedAt,
        String ward,
        boolean emergencyRelease,
        IssueOutcome outcome,
        Long patientId,
        Long bloodPackId) {

    public static IssueResponse from(Issue issue) {
        return new IssueResponse(issue.getId(), issue.getIssuedAt(), issue.getWard(),
                issue.isEmergencyRelease(), issue.getOutcome(),
                issue.getPatient().getId(), issue.getBloodPack().getId());
    }
}