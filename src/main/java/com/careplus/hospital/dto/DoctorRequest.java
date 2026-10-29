package com.careplus.hospital.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DoctorRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be 2 to 120 characters")
        String name,

        @NotNull(message = "Specialty is required")
        Long specialtyId,

        @NotBlank(message = "Qualifications are required")
        @Size(max = 255, message = "Qualifications must be at most 255 characters")
        String qualifications,

        @NotNull(message = "Experience is required")
        @Min(value = 0, message = "Experience cannot be negative")
        @Max(value = 70, message = "Experience is too high")
        Integer experienceYears,

        @Size(max = 500, message = "Image URL is too long")
        @Pattern(regexp = "^https?://\\S+$", message = "Image URL must start with http:// or https://")
        String profileImageUrl,

        @NotNull(message = "Slot minutes is required")
        @Min(value = 10, message = "Slot must be at least 10 minutes")
        @Max(value = 120, message = "Slot must be at most 120 minutes")
        Integer slotMinutes,

        Boolean active) {
	
}