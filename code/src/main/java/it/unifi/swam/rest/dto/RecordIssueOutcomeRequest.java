package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.IssueOutcome;

// RecordIssueOutcomeRequest.java
public record RecordIssueOutcomeRequest(
        IssueOutcome outcome) {
}