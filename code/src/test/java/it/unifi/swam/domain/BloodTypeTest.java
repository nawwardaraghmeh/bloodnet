package it.unifi.swam.domain;

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