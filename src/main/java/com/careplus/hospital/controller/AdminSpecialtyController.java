package com.careplus.hospital.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.careplus.hospital.dto.SpecialtyRequest;
import com.careplus.hospital.dto.SpecialtyResponse;
import com.careplus.hospital.service.AdminSpecialtyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/specialties")
public class AdminSpecialtyController {

    private final AdminSpecialtyService service;

    public AdminSpecialtyController(AdminSpecialtyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public SpecialtyResponse update(@PathVariable Long id, @Valid @RequestBody SpecialtyRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}