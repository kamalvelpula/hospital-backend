package com.careplus.hospital.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvailabilityUpdateRequest(
        @NotNull(message = "Availability list is required")
        @Size(max = 7, message = "At most 7 days allowed")
        List<@Valid AvailabilityRequest> availability) {
	
}