package it.unifi.swam.rest.controllers;

import it.unifi.swam.domain.*;
import it.unifi.swam.rest.dto.*;
import it.unifi.swam.services.TransfusionService;

// TransfusionController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/transfusion")
@Produces(MediaType.APPLICATION_JSON)
public class TransfusionController {

    @Inject
    TransfusionService transfusionService;

    @POST
    @Path("/patients")
    @Consumes(MediaType.APPLICATION_JSON)
    public PatientResponse registerPatient(RegisterPatientRequest request) {
        Patient patient = transfusionService.registerPatient(
                request.healthNum(), request.name(), request.dob(), request.bloodType());
        return PatientResponse.from(patient);
    }

    @POST
    @Path("/issues")
    @Consumes(MediaType.APPLICATION_JSON)
    public IssueResponse issuePackToPatient(IssuePackRequest request) {
        Issue issue = transfusionService.issuePackToPatient(
                request.packId(), request.patientId(), request.ward(),
                request.emergencyRelease());
        return IssueResponse.from(issue);
    }

    @POST
    @Path("/issues/{issueId}/outcome")
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordIssueOutcome(@PathParam("issueId") Long issueId,
                                   RecordIssueOutcomeRequest request) {
        transfusionService.recordIssueOutcome(issueId, request.outcome());
    }
}