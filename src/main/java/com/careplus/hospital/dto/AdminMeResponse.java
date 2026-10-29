package com.careplus.hospital.dto;

import java.util.List;

public record AdminMeResponse(
		String username, 
		List<String> roles) {
	
}