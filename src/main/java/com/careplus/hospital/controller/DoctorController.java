package com.careplus.hospital.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.careplus.hospital.dto.DoctorResponse;
import com.careplus.hospital.dto.SlotResponse;
import com.careplus.hospital.service.DoctorService;
import com.careplus.hospital.service.SlotService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final SlotService slotService;

    public DoctorController(DoctorService doctorService, SlotService slotService) {
        this.doctorService = doctorService;
        this.slotService = slotService;
    }

    @GetMapping
    public List<DoctorResponse> getDoctors(@RequestParam(required = false) Long specialtyId) {
        return doctorService.getDoctors(specialtyId);
    }

    @GetMapping("/{id}")
    public DoctorResponse getDoctor(@PathVariable Long id) {
        return doctorService.getDoctor(id);
    }

    @GetMapping("/{id}/slots")
    public List<SlotResponse> getSlots(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return slotService.getSlots(id, date);
    }
}