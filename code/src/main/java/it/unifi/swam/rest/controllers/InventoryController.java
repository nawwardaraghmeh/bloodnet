package it.unifi.swam.rest.controllers;

import it.unifi.swam.domain.StorageLocation;
import it.unifi.swam.rest.dto.*;
import it.unifi.swam.services.InventoryService;

// InventoryController.java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryController {

    @Inject
    InventoryService inventoryService;

    @POST
    @Path("/storage-locations")
    @Consumes(MediaType.APPLICATION_JSON)
    public StorageLocationResponse registerStorageLocation(RegisterStorageLocationRequest request) {
        StorageLocation location = inventoryService.registerStorageLocation(
                request.code(), request.minTempC(), request.maxTempC());
        return StorageLocationResponse.from(location);
    }

    @POST
    @Path("/blood-packs/{packId}/storage-locations/{locationId}")
    public void placePackInStorage(@PathParam("packId") Long packId,
                                   @PathParam("locationId") Long locationId) {
        inventoryService.placePackInStorage(packId, locationId);
    }

    @GET
    @Path("/storage-locations/{locationId}/blood-packs")
    public List<BloodPackResponse> listPacksInLocation(@PathParam("locationId") Long locationId) {
        return inventoryService.listPacksInLocation(locationId).stream()
                .map(BloodPackResponse::from)
                .toList();
    }

    @GET
    @Path("/blood-packs/{packId}")
    public BloodPackResponse viewBloodPack(@PathParam("packId") Long packId) {
        return BloodPackResponse.from(inventoryService.viewBloodPack(packId));
    }
}