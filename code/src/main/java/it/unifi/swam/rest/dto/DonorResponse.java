package it.unifi.swam.rest.dto;

import it.unifi.swam.domain.Donor;
import it.unifi.swam.domain.BloodType;

// DonorResponse.java
import java.time.LocalDate;

public record DonorResponse(
        Long id,
        String fiscalCode,
        String name,
        LocalDate dob,
        BloodType bloodType) {

    public static DonorResponse from(Donor donor) {
        return new DonorResponse(donor.getId(), donor.getFiscalCode(),
                donor.getName(), donor.getDob(), donor.getBloodType());
    }
}