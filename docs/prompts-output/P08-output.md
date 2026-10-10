## P08 — Domain classes → unit tests

```java
// BloodTypeTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloodTypeTest {

    @Test
    void canDonateTo_allCombinations() {
        BloodType[] types = BloodType.values();
        for (BloodType donor : types) {
            for (BloodType recipient : types) {
                boolean expected = expectedCompatibility(donor, recipient);
                assertEquals(expected, donor.canDonateTo(recipient),
                        donor + " -> " + recipient);
            }
        }
    }

    private boolean expectedCompatibility(BloodType donor, BloodType recipient) {
        Antigens d = antigens(donor);
        Antigens r = antigens(recipient);
        return (!d.hasA() || r.hasA())
                && (!d.hasB() || r.hasB())
                && (!d.hasRh() || r.hasRh());
    }

    private Antigens antigens(BloodType type) {
        switch (type) {
            case A_POSITIVE:  return new Antigens(true, false, true);
            case A_NEGATIVE:  return new Antigens(true, false, false);
            case B_POSITIVE:  return new Antigens(false, true, true);
            case B_NEGATIVE:  return new Antigens(false, true, false);
            case AB_POSITIVE: return new Antigens(true, true, true);
            case AB_NEGATIVE: return new Antigens(true, true, false);
            case O_POSITIVE:  return new Antigens(false, false, true);
            case O_NEGATIVE:  return new Antigens(false, false, false);
            default: throw new IllegalArgumentException("Unknown blood type: " + type);
        }
    }

    private record Antigens(boolean hasA, boolean hasB, boolean hasRh) {
    }
}
```

```java
// BloodPackTest.java
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BloodPackTest {

    private BloodPack createPack(BloodPackState state, LocalDateTime expiresAt, BloodType donorType) {
        Donor donor = new Donor("D1", "Donor", LocalDate.of(1990, 1, 1), donorType);
        Donation donation = new Donation(LocalDateTime.now(), "Site", 450.0,
                ScreeningStatus.PENDING, donor);
        return new BloodPack("P1", LocalDateTime.now(), expiresAt, state,
                true, true, true, donation);
    }

    // markAvailable
    @Test
    void markAvailable_allowed() {
        BloodPack pack = createPack(BloodPackState.QUARANTINED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        pack.markAvailable();
        assertEquals(BloodPackState.AVAILABLE, pack.getState());
    }

    @Test
    void markAvailable_disallowed() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertThrows(IllegalStateException.class, pack::markAvailable);
    }

    // discard
    @Test
    void discard_allowed() {
        BloodPack pack = createPack(BloodPackState.QUARANTINED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        pack.discard();
        assertEquals(BloodPackState.DISCARDED, pack.getState());
    }

    @Test
    void discard_disallowed() {
        BloodPack pack = createPack(BloodPackState.TRANSFUSED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertThrows(IllegalStateException.class, pack::discard);
    }

    // issue
    @Test
    void issue_allowed() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        pack.issue();
        assertEquals(BloodPackState.ISSUED, pack.getState());
    }

    @Test
    void issue_disallowed() {
        BloodPack pack = createPack(BloodPackState.QUARANTINED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertThrows(IllegalStateException.class, pack::issue);
    }

    // returnToAvailable
    @Test
    void returnToAvailable_allowed() {
        BloodPack pack = createPack(BloodPackState.ISSUED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        pack.returnToAvailable();
        assertEquals(BloodPackState.AVAILABLE, pack.getState());
    }

    @Test
    void returnToAvailable_disallowed() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertThrows(IllegalStateException.class, pack::returnToAvailable);
    }

    // markTransfused
    @Test
    void markTransfused_allowed() {
        BloodPack pack = createPack(BloodPackState.ISSUED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        pack.markTransfused();
        assertEquals(BloodPackState.TRANSFUSED, pack.getState());
    }

    @Test
    void markTransfused_disallowed() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertThrows(IllegalStateException.class, pack::markTransfused);
    }

    // isExpired
    @Test
    void isExpired_true() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().minusDays(1), BloodType.O_NEGATIVE);
        assertTrue(pack.isExpired());
    }

    @Test
    void isExpired_false() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertFalse(pack.isExpired());
    }

    // canBeIssued
    @Test
    void canBeIssued_true() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertTrue(pack.canBeIssued());
    }

    @Test
    void canBeIssued_false_expired() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().minusDays(1), BloodType.O_NEGATIVE);
        assertFalse(pack.canBeIssued());
    }

    @Test
    void canBeIssued_false_notAvailable() {
        BloodPack pack = createPack(BloodPackState.QUARANTINED,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        assertFalse(pack.canBeIssued());
    }

    // isCompatibleWith
    @Test
    void isCompatibleWith_compatible() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.O_NEGATIVE);
        Patient patient = new Patient("P1", "Patient",
                LocalDate.of(1980, 1, 1), BloodType.A_POSITIVE);
        assertTrue(pack.isCompatibleWith(patient));
    }

    @Test
    void isCompatibleWith_incompatible() {
        BloodPack pack = createPack(BloodPackState.AVAILABLE,
                LocalDateTime.now().plusDays(1), BloodType.A_POSITIVE);
        Patient patient = new Patient("P1", "Patient",
                LocalDate.of(1980, 1, 1), BloodType.O_NEGATIVE);
        assertFalse(pack.isCompatibleWith(patient));
    }
}
```