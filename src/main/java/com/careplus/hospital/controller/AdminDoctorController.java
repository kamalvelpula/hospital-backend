package com.careplus.hospital.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.careplus.hospital.dto.AdminDoctorResponse;
import com.careplus.hospital.dto.AvailabilityUpdateRequest;
import com.careplus.hospital.dto.DoctorRequest;
import com.careplus.hospital.service.AdminDoctorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/doctors")
public class AdminDoctorController {

    private final AdminDoctorService service;

    public AdminDoctorController(AdminDoctorService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminDoctorResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public AdminDoctorResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<AdminDoctorResponse> create(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public AdminDoctorResponse update(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/availability")
    public AdminDoctorResponse updateAvailability(@PathVariable Long id,
                                                  @Valid @RequestBody AvailabilityUpdateRequest request) {
        return service.updateAvailability(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}