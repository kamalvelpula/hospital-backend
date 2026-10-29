package com.careplus.hospital.dto;

public record LoginResponse(
		String token, 
		String tokenType, 
		long expiresInSeconds, 
		String username, 
		String fullName) {
	
}