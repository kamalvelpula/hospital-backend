package com.careplus.hospital.service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.AdminDoctorResponse;
import com.careplus.hospital.dto.AvailabilityRequest;
import com.careplus.hospital.dto.AvailabilityResponse;
import com.careplus.hospital.dto.AvailabilityUpdateRequest;
import com.careplus.hospital.dto.DoctorRequest;
import com.careplus.hospital.entity.Doctor;
import com.careplus.hospital.entity.DoctorAvailability;
import com.careplus.hospital.entity.Specialty;
import com.careplus.hospital.exception.BadRequestException;
import com.careplus.hospital.exception.ConflictException;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.AppointmentRepository;
import com.careplus.hospital.repository.DoctorRepository;
import com.careplus.hospital.repository.SpecialtyRepository;

@Service
public class AdminDoctorService {

    private static final Logger log = LoggerFactory.getLogger(AdminDoctorService.class);

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminDoctorService(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository,
                              AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminDoctorResponse> list() {
        return doctorRepository.findAllWithSpecialty().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AdminDoctorResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public AdminDoctorResponse create(DoctorRequest req) {
        Doctor d = new Doctor();
        apply(d, req);
        d.setActive(req.active() == null || req.active());
        doctorRepository.save(d);
        log.info("Doctor created: id={}", d.getId());
        return toResponse(d);
    }

    @Transactional
    public AdminDoctorResponse update(Long id, DoctorRequest req) {
        Doctor d = find(id);
        apply(d, req);
        if (req.active() != null) {
            d.setActive(req.active());
        }
        doctorRepository.save(d);
        log.info("Doctor updated: id={}", id);
        return toResponse(d);
    }

    @Transactional
    public AdminDoctorResponse updateAvailability(Long id, AvailabilityUpdateRequest req) {
        Doctor d = find(id);

        Set<Integer> requested = new HashSet<>();
        for (AvailabilityRequest r : req.availability()) {
            if (!requested.add(r.dayOfWeek())) {
                throw new BadRequestException("Duplicate entry for day " + r.dayOfWeek());
            }
            if (!r.endTime().isAfter(r.startTime())) {
                throw new BadRequestException("End time must be after start time for day " + r.dayOfWeek());
            }
        }

        // Update in place so the unique (doctor, day) constraint is never violated
        Map<Integer, DoctorAvailability> existing = new HashMap<>();
        d.getAvailability().forEach(a -> existing.put(a.getDayOfWeek(), a));
        d.getAvailability().removeIf(a -> !requested.contains(a.getDayOfWeek()));

        for (AvailabilityRequest r : req.availability()) {
            DoctorAvailability a = existing.get(r.dayOfWeek());
            if (a == null) {
                a = new DoctorAvailability();
                a.setDoctor(d);
                a.setDayOfWeek(r.dayOfWeek());
                d.getAvailability().add(a);
            }
            a.setStartTime(r.startTime());
            a.setEndTime(r.endTime());
        }
        doctorRepository.save(d);
        log.info("Doctor availability updated: id={}", id);
        return toResponse(d);
    }

    @Transactional
    public void delete(Long id) {
        Doctor d = find(id);
        if (appointmentRepository.existsByDoctorId(id)) {
            throw new ConflictException("This doctor has appointments and cannot be deleted. Deactivate the doctor instead.");
        }
        doctorRepository.delete(d);
        log.info("Doctor deleted: id={}", id);
    }

    private void apply(Doctor d, DoctorRequest req) {
        Specialty specialty = specialtyRepository.findById(req.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));
        d.setName(clean(req.name()));
        d.setSpecialty(specialty);
        d.setQualifications(clean(req.qualifications()));
        d.setExperienceYears(req.experienceYears());
        d.setProfileImageUrl(req.profileImageUrl() == null || req.profileImageUrl().isBlank()
                ? null : req.profileImageUrl().trim());
        d.setSlotMinutes(req.slotMinutes());
    }

    private Doctor find(Long id) {
        return doctorRepository.findByIdWithSpecialty(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    private AdminDoctorResponse toResponse(Doctor d) {
        List<AvailabilityResponse> availability = d.getAvailability().stream()
                .sorted(Comparator.comparingInt(a -> a.getDayOfWeek()))
                .map(a -> new AvailabilityResponse(
                        a.getDayOfWeek(),
                        DayOfWeek.of(a.getDayOfWeek()).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        a.getStartTime(),
                        a.getEndTime()))
                .toList();
        return new AdminDoctorResponse(d.getId(), d.getName(), d.getSpecialty().getId(), d.getSpecialty().getName(),
                d.getQualifications(), d.getExperienceYears(), d.getProfileImageUrl(), d.getSlotMinutes(),
                d.isActive(), availability);
    }

    private String clean(String v) {
        return v.trim().replaceAll("[<>]", "");
    }
}