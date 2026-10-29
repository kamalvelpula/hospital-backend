package com.careplus.hospital.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.careplus.hospital.entity.DoctorAvailability;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorIdOrderByDayOfWeek(Long doctorId);
    Optional<DoctorAvailability> findByDoctorIdAndDayOfWeek(Long doctorId, int dayOfWeek);
}