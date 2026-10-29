package com.careplus.hospital.dto;

import java.util.List;

public record DoctorResponse(
        Long id,
        String name,
        Long specialtyId,
        String specialtyName,
        String qualifications,
        int experienceYears,
        String profileImageUrl,
        int slotMinutes,
        List<AvailabilityResponse> availability) {
	
}