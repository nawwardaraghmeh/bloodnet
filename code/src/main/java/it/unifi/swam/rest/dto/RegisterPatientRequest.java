package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.BloodType;

// RegisterPatientRequest.java
import java.time.LocalDate;

public record RegisterPatientRequest(
        String healthNum,
        String name,
        LocalDate dob,
        BloodType bloodType) {
}