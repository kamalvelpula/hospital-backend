package com.careplus.hospital.dto;

import java.time.LocalTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(
        @NotNull(message = "Day of week is required")
        @Min(value = 1, message = "Day of week must be 1 (Monday) to 7 (Sunday)")
        @Max(value = 7, message = "Day of week must be 1 (Monday) to 7 (Sunday)")
        Integer dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime) {
	
}