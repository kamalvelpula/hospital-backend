package com.careplus.hospital.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.careplus.hospital.entity.Appointment;
import com.careplus.hospital.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long doctorId, LocalDate date, LocalTime time, Collection<AppointmentStatus> statuses);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusIn(
            Long doctorId, LocalDate date, Collection<AppointmentStatus> statuses);

    Optional<Appointment> findByBookingReferenceAndPatientPhone(String bookingReference, String patientPhone);

    boolean existsByBookingReference(String bookingReference);

    long countByStatus(AppointmentStatus status);

    boolean existsByDoctorId(Long doctorId);

    @Query(value = """
            select a from Appointment a
            join fetch a.doctor d
            join fetch d.specialty
            where (:status is null or a.status = :status)
              and (:date is null or a.appointmentDate = :date)
              and (:doctorId is null or d.id = :doctorId)
              and (:search is null
                   or lower(a.patientName) like :search
                   or lower(a.bookingReference) like :search
                   or a.patientPhone like :search)
            order by a.appointmentDate desc, a.appointmentTime desc
            """,
            countQuery = """
            select count(a) from Appointment a
            where (:status is null or a.status = :status)
              and (:date is null or a.appointmentDate = :date)
              and (:doctorId is null or a.doctor.id = :doctorId)
              and (:search is null
                   or lower(a.patientName) like :search
                   or lower(a.bookingReference) like :search
                   or a.patientPhone like :search)
            """)
    Page<Appointment> search(@Param("status") AppointmentStatus status,
                             @Param("date") LocalDate date,
                             @Param("doctorId") Long doctorId,
                             @Param("search") String search,
                             Pageable pageable);
}