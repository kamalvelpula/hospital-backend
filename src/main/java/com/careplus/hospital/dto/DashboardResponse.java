package com.careplus.hospital.dto;

public record DashboardResponse(
		long total, 
		long pending, 
		long confirmed, 
		long completed, 
		long cancelled) {
	
}