package com.careplus.hospital.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AdminAppointmentResponse(
        Long id,
        String bookingReference,
        String patientName,
        String patientPhone,
        String patientEmail,
        Long doctorId,
        String doctorName,
        String specialtyName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String reason,
        String status,
        LocalDateTime createdAt) {
	
}