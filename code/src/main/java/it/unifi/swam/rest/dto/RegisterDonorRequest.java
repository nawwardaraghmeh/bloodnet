package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.BloodType;

// RegisterDonorRequest.java
import java.time.LocalDate;

public record RegisterDonorRequest(
        String fiscalCode,
        String name,
        LocalDate dob,
        BloodType bloodType) {
}