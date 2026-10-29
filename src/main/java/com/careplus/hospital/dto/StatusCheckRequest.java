package com.careplus.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StatusCheckRequest(
        @NotBlank(message = "Booking reference is required")
        @Size(max = 20, message = "Invalid booking reference")
        String bookingReference,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
        String phone) {
	
}