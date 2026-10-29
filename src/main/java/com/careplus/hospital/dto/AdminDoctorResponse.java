package com.careplus.hospital.dto;

import java.util.List;

public record AdminDoctorResponse(
        Long id,
        String name,
        Long specialtyId,
        String specialtyName,
        String qualifications,
        int experienceYears,
        String profileImageUrl,
        int slotMinutes,
        boolean active,
        List<AvailabilityResponse> availability) {
	
}