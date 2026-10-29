package com.careplus.hospital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import com.careplus.hospital.dto.AdminAppointmentResponse;
import com.careplus.hospital.dto.DashboardResponse;
import com.careplus.hospital.dto.PageResponse;
import com.careplus.hospital.entity.Appointment;
import com.careplus.hospital.entity.AppointmentStatus;
import com.careplus.hospital.exception.BadRequestException;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.AppointmentRepository;

@Service
public class AdminAppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AdminAppointmentService.class);

    private final AppointmentRepository appointmentRepository;

    public AdminAppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                appointmentRepository.count(),
                appointmentRepository.countByStatus(AppointmentStatus.PENDING),
                appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED),
                appointmentRepository.countByStatus(AppointmentStatus.COMPLETED),
                appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminAppointmentResponse> search(AppointmentStatus status, LocalDate date, Long doctorId,
                                                         String search, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        String pattern = null;
        if (search != null && !search.isBlank()) {
            String s = search.trim().toLowerCase();
            if (s.length() > 50) {
                s = s.substring(0, 50);
            }
            pattern = "%" + s + "%";
        }

        Page<Appointment> result = appointmentRepository.search(status, date, doctorId, pattern,
                PageRequest.of(safePage, safeSize));

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AdminAppointmentResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public AdminAppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = find(id);
        AppointmentStatus current = appointment.getStatus();
        validateTransition(current, newStatus);
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        log.info("Appointment status updated: ref={}, {} -> {}", appointment.getBookingReference(), current, newStatus);
        return toResponse(appointment);
    }

    private void validateTransition(AppointmentStatus from, AppointmentStatus to) {
        boolean allowed = switch (from) {
            case PENDING -> to == AppointmentStatus.CONFIRMED || to == AppointmentStatus.CANCELLED;
            case CONFIRMED -> to == AppointmentStatus.COMPLETED || to == AppointmentStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
        if (!allowed) {
            throw new BadRequestException("Cannot change status from " + from + " to " + to);
        }
    }

    private Appointment find(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private AdminAppointmentResponse toResponse(Appointment a) {
        return new AdminAppointmentResponse(
                a.getId(),
                a.getBookingReference(),
                a.getPatientName(),
                a.getPatientPhone(),
                a.getPatientEmail(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getDoctor().getSpecialty().getName(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getReason(),
                a.getStatus().name(),
                a.getCreatedAt());
    }
}