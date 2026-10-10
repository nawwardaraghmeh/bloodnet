package it.unifi.swam.rest.controllers;

import it.unifi.swam.domain.*;
import it.unifi.swam.rest.dto.*;
import it.unifi.swam.services.*;

// DonationController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/collection")
@Produces(MediaType.APPLICATION_JSON)
public class DonationController {

    @Inject
    DonationService donationService;

    @POST
    @Path("/donors")
    @Consumes(MediaType.APPLICATION_JSON)
    public DonorResponse registerDonor(RegisterDonorRequest request) {
        Donor donor = donationService.registerDonor(
                request.fiscalCode(), request.name(), request.dob(), request.bloodType());
        return DonorResponse.from(donor);
    }

    @POST
    @Path("/donations")
    @Consumes(MediaType.APPLICATION_JSON)
    public DonationResponse recordDonation(RecordDonationRequest request) {
        List<BloodPackSpec> specs = request.packs().stream()
                .map(p -> new BloodPackSpec(p.packCode(), p.producedAt(), p.expiresAt(),
                        p.redCells(), p.plasma(), p.platelets()))
                .toList();
        Donation donation = donationService.recordDonation(
                request.collectedAt(), request.collectionSite(), request.volume(),
                request.donorId(), specs);
        return DonationResponse.from(donation);
    }

    @POST
    @Path("/donations/{donationId}/screening")
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordScreeningResult(@PathParam("donationId") Long donationId,
                                      RecordScreeningResultRequest request) {
        donationService.recordScreeningResult(donationId, request.status());
    }
}