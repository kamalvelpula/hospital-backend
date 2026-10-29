package com.careplus.hospital.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.careplus.hospital.dto.AppointmentRequest;
import com.careplus.hospital.dto.AppointmentResponse;
import com.careplus.hospital.dto.StatusCheckRequest;
import com.careplus.hospital.service.AppointmentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(request));
    }

    @PostMapping("/status")
    public AppointmentResponse status(@Valid @RequestBody StatusCheckRequest request) {
        return appointmentService.checkStatus(request);
    }
}