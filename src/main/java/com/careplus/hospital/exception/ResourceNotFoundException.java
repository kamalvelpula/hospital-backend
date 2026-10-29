package com.careplus.hospital.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { 
    	super(message); 
    	}
}