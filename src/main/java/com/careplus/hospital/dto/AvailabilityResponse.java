package com.careplus.hospital.dto;

import java.time.LocalTime;

public record AvailabilityResponse(
		int dayOfWeek, String dayName, 
		LocalTime startTime, 
		LocalTime endTime) {
	
}