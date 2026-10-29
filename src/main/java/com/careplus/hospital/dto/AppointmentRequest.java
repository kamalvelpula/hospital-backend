package com.careplus.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.*;

public record AppointmentRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be 2 to 120 characters")
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name contains invalid characters")
        String patientName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
        String patientPhone,

        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email is too long")
        String patientEmail,

        @NotNull(message = "Doctor is required")
        Long doctorId,

        @NotNull(message = "Date is required")
        @FutureOrPresent(message = "Appointment date cannot be in the past")
        LocalDate appointmentDate,

        @NotNull(message = "Time is required")
        LocalTime appointmentTime,

        @NotBlank(message = "Reason is required")
        @Size(min = 5, max = 300, message = "Reason must be 5 to 300 characters")
        String reason) {
	
}