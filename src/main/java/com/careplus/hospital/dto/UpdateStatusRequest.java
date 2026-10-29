package com.careplus.hospital.dto;

import com.careplus.hospital.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        AppointmentStatus status) {
	
}