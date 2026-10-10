package it.unifi.swam.rest.dto;

// BloodPackRequest.java
import java.time.LocalDateTime;

public record BloodPackRequest(
        String packCode,
        LocalDateTime producedAt,
        LocalDateTime expiresAt,
        boolean redCells,
        boolean plasma,
        boolean platelets) {
}