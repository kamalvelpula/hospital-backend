package com.careplus.hospital.dto;

import java.time.LocalTime;

public record SlotResponse(
		LocalTime time, 
		boolean available) {
	
}