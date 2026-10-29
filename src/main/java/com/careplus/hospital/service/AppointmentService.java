package com.careplus.hospital.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.AppointmentRequest;
import com.careplus.hospital.dto.AppointmentResponse;
import com.careplus.hospital.dto.SlotResponse;
import com.careplus.hospital.dto.StatusCheckRequest;
import com.careplus.hospital.entity.Appointment;
import com.careplus.hospital.entity.AppointmentStatus;
import com.careplus.hospital.entity.Doctor;
import com.careplus.hospital.exception.BadRequestException;
import com.careplus.hospital.exception.ConflictException;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.AppointmentRepository;
import com.careplus.hospital.repository.DoctorRepository;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private static final String REF_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SlotService slotService;
    private final SecureRandom random = new SecureRandom();

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              SlotService slotService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.slotService = slotService;
    }

    @Transactional
    public AppointmentResponse book(AppointmentRequest req) {
        // Row lock on the doctor: bookings for the same doctor are processed one at a time
        Doctor doctor = doctorRepository.findByIdForUpdate(req.doctorId())
                .filter(Doctor::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        LocalDate date = req.appointmentDate();
        LocalTime time = req.appointmentTime().withSecond(0).withNano(0);

        SlotResponse slot = slotService.slotsFor(doctor, date).stream()
                .filter(s -> s.time().equals(time))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Doctor is not available at the selected date and time"));

        if (!slot.available()) {
            throw new ConflictException("This time slot is no longer available. Please choose another slot.");
        }

        Appointment appointment = new Appointment();
        appointment.setBookingReference(generateReference());
        appointment.setPatientName(clean(req.patientName()));
        appointment.setPatientPhone(req.patientPhone());
        appointment.setPatientEmail(blankToNull(req.patientEmail()));
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setReason(clean(req.reason()));
        appointment.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);

        // Log only non-sensitive identifiers
        log.info("Appointment created: ref={}, doctorId={}", appointment.getBookingReference(), doctor.getId());

        return toResponse(appointment,
                "Your appointment request has been received. Keep your booking reference to check the status.");
    }

    @Transactional(readOnly = true)
    public AppointmentResponse checkStatus(StatusCheckRequest req) {
        String ref = req.bookingReference().trim().toUpperCase();
        Appointment appointment = appointmentRepository
                .findByBookingReferenceAndPatientPhone(ref, req.phone())
                .orElseThrow(() -> new ResourceNotFoundException("No appointment found for the given details"));
        return toResponse(appointment, statusMessage(appointment.getStatus()));
    }

    private AppointmentResponse toResponse(Appointment a, String message) {
        return new AppointmentResponse(
                a.getBookingReference(),
                a.getDoctor().getName(),
                a.getDoctor().getSpecialty().getName(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getStatus().name(),
                message);
    }

    private String statusMessage(AppointmentStatus status) {
        return switch (status) {
            case PENDING -> "Your request is received and awaiting confirmation.";
            case CONFIRMED -> "Your appointment is confirmed. Please arrive 15 minutes early.";
            case COMPLETED -> "This appointment has been completed.";
            case CANCELLED -> "This appointment was cancelled. Please book a new one if needed.";
        };
    }

    private String generateReference() {
        for (int attempt = 0; attempt < 5; attempt++) {
            StringBuilder sb = new StringBuilder("CP-");
            for (int i = 0; i < 8; i++) {
                sb.append(REF_ALPHABET.charAt(random.nextInt(REF_ALPHABET.length())));
            }
            String ref = sb.toString();
            if (!appointmentRepository.existsByBookingReference(ref)) {
                return ref;
            }
        }
        throw new IllegalStateException("Could not generate a unique booking reference");
    }

    private String clean(String value) {
        return value.trim().replaceAll("[<>]", "");
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim().toLowerCase();
    }
}