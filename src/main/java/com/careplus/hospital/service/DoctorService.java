package com.careplus.hospital.service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.AvailabilityResponse;
import com.careplus.hospital.dto.DoctorResponse;
import com.careplus.hospital.entity.Doctor;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctors(Long specialtyId) {
        List<Doctor> doctors = (specialtyId == null)
                ? doctorRepository.findAllActive()
                : doctorRepository.findActiveBySpecialty(specialtyId);
        return doctors.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctor(Long id) {
        Doctor doctor = doctorRepository.findByIdWithSpecialty(id)
                .filter(Doctor::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return toResponse(doctor);
    }

    // Must run inside a transaction because availability is lazy-loaded
    private DoctorResponse toResponse(Doctor d) {
        List<AvailabilityResponse> availability = d.getAvailability().stream()
                .sorted(Comparator.comparingInt(a -> a.getDayOfWeek()))
                .map(a -> new AvailabilityResponse(
                        a.getDayOfWeek(),
                        DayOfWeek.of(a.getDayOfWeek()).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        a.getStartTime(),
                        a.getEndTime()))
                .toList();
        return new DoctorResponse(d.getId(), d.getName(), d.getSpecialty().getId(), d.getSpecialty().getName(),
                d.getQualifications(), d.getExperienceYears(), d.getProfileImageUrl(), d.getSlotMinutes(), availability);
    }
}