package com.careplus.hospital.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.SlotResponse;
import com.careplus.hospital.entity.Appointment;
import com.careplus.hospital.entity.AppointmentStatus;
import com.careplus.hospital.entity.Doctor;
import com.careplus.hospital.exception.BadRequestException;
import com.careplus.hospital.exception.ResourceNotFoundException;
import com.careplus.hospital.repository.AppointmentRepository;
import com.careplus.hospital.repository.DoctorAvailabilityRepository;
import com.careplus.hospital.repository.DoctorRepository;

@Service
public class SlotService {

    // Cancelled and completed appointments do not block a slot
    public static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final Clock clock;

    public SlotService(DoctorRepository doctorRepository,
                       DoctorAvailabilityRepository availabilityRepository,
                       AppointmentRepository appointmentRepository,
                       Clock clock) {
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .filter(Doctor::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return slotsFor(doctor, date);
    }

    public List<SlotResponse> slotsFor(Doctor doctor, LocalDate date) {
        LocalDate today = LocalDate.now(clock);
        if (date.isBefore(today)) {
            throw new BadRequestException("Past dates are not allowed");
        }

        Set<LocalTime> booked = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatusIn(doctor.getId(), date, ACTIVE_STATUSES)
                .stream().map(Appointment::getAppointmentTime).collect(Collectors.toSet());

        LocalTime now = LocalTime.now(clock);
        boolean isToday = date.equals(today);

        return generateSlots(doctor, date).stream()
                .map(t -> new SlotResponse(t, !booked.contains(t) && !(isToday && !t.isAfter(now))))
                .toList();
    }

    private List<LocalTime> generateSlots(Doctor doctor, LocalDate date) {
        int step = doctor.getSlotMinutes();
        if (step <= 0) {
            return List.of();
        }
        return availabilityRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), date.getDayOfWeek().getValue())
                .map(a -> {
                    List<LocalTime> slots = new ArrayList<>();
                    int start = a.getStartTime().toSecondOfDay() / 60;
                    int end = a.getEndTime().toSecondOfDay() / 60;
                    for (int m = start; m + step <= end; m += step) {
                        slots.add(LocalTime.ofSecondOfDay(m * 60L));
                    }
                    return slots;
                })
                .orElse(List.of());
    }
}