package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.Patient;
import it.unifi.swam.domain.BloodType;

// PatientResponse.java
import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String healthNum,
        String name,
        LocalDate dob,
        BloodType bloodType) {

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getHealthNum(),
                patient.getName(), patient.getDob(), patient.getBloodType());
    }
}