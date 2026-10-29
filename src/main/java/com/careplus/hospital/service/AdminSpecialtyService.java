package com.careplus.hospital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.SpecialtyRequest;
import com.careplus.hospital.dto.SpecialtyResponse;
import com.careplus.hospital.entity.Specialty;
import com.careplus.hospital.exception.ConflictException;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.DoctorRepository;
import com.careplus.hospital.repository.SpecialtyRepository;

@Service
public class AdminSpecialtyService {

    private static final Logger log = LoggerFactory.getLogger(AdminSpecialtyService.class);

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    public AdminSpecialtyService(SpecialtyRepository specialtyRepository, DoctorRepository doctorRepository) {
        this.specialtyRepository = specialtyRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public SpecialtyResponse create(SpecialtyRequest req) {
        String name = req.name().trim();
        if (specialtyRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A specialty with this name already exists");
        }
        Specialty s = new Specialty();
        s.setName(name);
        s.setDescription(blankToNull(req.description()));
        specialtyRepository.save(s);
        log.info("Specialty created: id={}", s.getId());
        return toResponse(s);
    }

    @Transactional
    public SpecialtyResponse update(Long id, SpecialtyRequest req) {
        Specialty s = find(id);
        String name = req.name().trim();
        if (specialtyRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("A specialty with this name already exists");
        }
        s.setName(name);
        s.setDescription(blankToNull(req.description()));
        specialtyRepository.save(s);
        log.info("Specialty updated: id={}", id);
        return toResponse(s);
    }

    @Transactional
    public void delete(Long id) {
        Specialty s = find(id);
        if (doctorRepository.existsBySpecialtyId(id)) {
            throw new ConflictException("This specialty has doctors and cannot be deleted");
        }
        specialtyRepository.delete(s);
        log.info("Specialty deleted: id={}", id);
    }

    private Specialty find(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));
    }

    private SpecialtyResponse toResponse(Specialty s) {
        return new SpecialtyResponse(s.getId(), s.getName(), s.getDescription());
    }

    private String blankToNull(String v) {
        return (v == null || v.isBlank()) ? null : v.trim().replaceAll("[<>]", "");
    }
}