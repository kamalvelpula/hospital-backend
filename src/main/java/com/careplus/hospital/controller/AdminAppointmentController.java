package com.careplus.hospital.controller;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.careplus.hospital.dto.AdminAppointmentResponse;
import com.careplus.hospital.dto.DashboardResponse;
import com.careplus.hospital.dto.PageResponse;
import com.careplus.hospital.dto.UpdateStatusRequest;
import com.careplus.hospital.entity.AppointmentStatus;
import com.careplus.hospital.service.AdminAppointmentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminAppointmentController {

    private final AdminAppointmentService service;

    public AdminAppointmentController(AdminAppointmentService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.getDashboard();
    }

    @GetMapping("/appointments")
    public PageResponse<AdminAppointmentResponse> list(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.search(status, date, doctorId, search, page, size);
    }

    @GetMapping("/appointments/{id}")
    public AdminAppointmentResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/appointments/{id}/status")
    public AdminAppointmentResponse updateStatus(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(id, request.status());
    }
}