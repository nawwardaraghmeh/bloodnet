package it.unifi.swam.rest;

// BloodDonationE2ETest.java
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class BloodDonationE2ETest {

    // ------------------------------------------------------------------
    // happy path
    // ------------------------------------------------------------------

    @Test
    void fullFlow() {
        // 1. register donor
        long donorId = registerDonor("D1", "O_NEGATIVE");

        // 2. register patient
        long patientId = registerPatient("P1", "A_POSITIVE");

        // 3. register storage location
        long locationId = registerStorageLocation("L1", 2.0, 6.0);

        // 4. record donation producing two packs
        JsonPath donation = recordDonation(donorId, "PACK1", "PACK2");
        long donationId = donation.getLong("id");
        List<Long> packIds = donation.getList("packIds", Long.class);
        long packId = packIds.get(0);

        // state after donation: QUARANTINED
        assertPackState(packId, "QUARANTINED");

        // 5. record passing screening result
        recordScreening(donationId, "PASSED");

        // state after screening: AVAILABLE
        assertPackState(packId, "AVAILABLE");

        // 6. place pack into storage
        given()
                .when()
                .post("/inventory/blood-packs/" + packId + "/storage-locations/" + locationId)
                .then()
                .statusCode(204);

        // 7. list packs in location
        given()
                .when()
                .get("/inventory/storage-locations/" + locationId + "/blood-packs")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo((int) packId))
                .body("[0].state", equalTo("AVAILABLE"));

        // 8. issue pack to patient
        long issueId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "packId": %d,
                            "patientId": %d,
                            "ward": "W1",
                            "emergencyRelease": false
                        }
                        """.formatted(packId, patientId))
                .when()
                .post("/transfusion/issues")
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("id");

        // state after issue: ISSUED
        assertPackState(packId, "ISSUED");

        // 9. record outcome transfused
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "outcome": "TRANSFUSED"
                        }
                        """)
                .when()
                .post("/transfusion/issues/" + issueId + "/outcome")
                .then()
                .statusCode(204);

        // state after outcome: TRANSFUSED
        assertPackState(packId, "TRANSFUSED");
    }

    // ------------------------------------------------------------------
    // failures
    // ------------------------------------------------------------------

    @Test
    void screeningForNonExistentDonationReturns404() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "status": "PASSED"
                        }
                        """)
                .when()
                .post("/collection/donations/999999/screening")
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    void screeningTwiceReturns409() {
        long donorId = registerDonor("D2", "O_NEGATIVE");
        long donationId = recordDonation(donorId, "PACK3", "PACK4").getLong("id");

        recordScreening(donationId, "PASSED");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "status": "PASSED"
                        }
                        """)
                .when()
                .post("/collection/donations/" + donationId + "/screening")
                .then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    @Test
    void issuingNotAvailablePackReturns409() {
        long donorId = registerDonor("D3", "O_NEGATIVE");
        long patientId = registerPatient("P3", "A_POSITIVE");
        JsonPath donation = recordDonation(donorId, "PACK5");
        long packId = donation.getList("packIds", Long.class).get(0);

        // pack is still QUARANTINED, never screened
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "packId": %d,
                            "patientId": %d,
                            "ward": "W1",
                            "emergencyRelease": false
                        }
                        """.formatted(packId, patientId))
                .when()
                .post("/transfusion/issues")
                .then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    @Test
    void issuingIncompatiblePackReturns409() {
        long donorId = registerDonor("D4", "O_POSITIVE");
        long patientId = registerPatient("P4", "A_NEGATIVE");
        JsonPath donation = recordDonation(donorId, "PACK6");
        long donationId = donation.getLong("id");
        long packId = donation.getList("packIds", Long.class).get(0);

        // make the pack AVAILABLE so it is issuable from a state point of view;
        // the only thing that must reject the issue is blood type incompatibility
        recordScreening(donationId, "PASSED");
        assertPackState(packId, "AVAILABLE");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "packId": %d,
                            "patientId": %d,
                            "ward": "W1",
                            "emergencyRelease": false
                        }
                        """.formatted(packId, patientId))
                .when()
                .post("/transfusion/issues")
                .then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    // ------------------------------------------------------------------
    // helpers, HTTP only
    // ------------------------------------------------------------------

    private long registerDonor(String fiscalCode, String bloodType) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "fiscalCode": "%s",
                            "name": "Donor",
                            "dob": "1990-01-01",
                            "bloodType": "%s"
                        }
                        """.formatted(fiscalCode, bloodType))
                .when()
                .post("/collection/donors")
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private long registerPatient(String healthNum, String bloodType) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "healthNum": "%s",
                            "name": "Patient",
                            "dob": "1980-01-01",
                            "bloodType": "%s"
                        }
                        """.formatted(healthNum, bloodType))
                .when()
                .post("/transfusion/patients")
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private long registerStorageLocation(String code, double minTempC, double maxTempC) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "code": "%s",
                            "minTempC": %s,
                            "maxTempC": %s
                        }
                        """.formatted(code, minTempC, maxTempC))
                .when()
                .post("/inventory/storage-locations")
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private JsonPath recordDonation(long donorId, String... packCodes) {
        StringBuilder packs = new StringBuilder();
        for (int i = 0; i < packCodes.length; i++) {
            if (i > 0) {
                packs.append(",");
            }
            packs.append("""
                    {
                        "packCode": "%s",
                        "producedAt": "2025-01-01T10:05:00",
                        "expiresAt": "2099-01-01T10:05:00",
                        "redCells": true,
                        "plasma": true,
                        "platelets": true
                    }
                    """.formatted(packCodes[i]));
        }
        String json = """
                {
                    "collectedAt": "2025-01-01T10:00:00",
                    "collectionSite": "Site",
                    "volume": 450.0,
                    "donorId": %d,
                    "packs": [%s]
                }
                """.formatted(donorId, packs.toString());

        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/collection/donations")
                .then()
                .statusCode(200)
                .extract().jsonPath();
    }

    private void recordScreening(long donationId, String status) {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .when()
                .post("/collection/donations/" + donationId + "/screening")
                .then()
                .statusCode(204);
    }

    private void assertPackState(long packId, String expectedState) {
        given()
                .when()
                .get("/inventory/blood-packs/" + packId)
                .then()
                .statusCode(200)
                .body("state", equalTo(expectedState));
    }
}