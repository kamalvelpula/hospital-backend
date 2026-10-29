package com.careplus.hospital.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(
    		String message) {
    	super(message); 
    	}
}