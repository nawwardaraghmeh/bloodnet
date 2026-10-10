package it.unifi.swam.services;

// BloodPackSpec.java
import java.time.LocalDateTime;

public record BloodPackSpec(
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        boolean redCells,
        boolean plasma,
        boolean platelets) {
}
