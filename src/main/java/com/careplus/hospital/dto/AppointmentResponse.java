package com.careplus.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

// Deliberately excludes phone, email and reason to avoid exposing patient information.
public record AppointmentResponse(
        String bookingReference,
        String doctorName,
        String specialtyName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status,
        String message) {
	
}