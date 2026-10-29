package com.careplus.hospital.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import com.careplus.hospital.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("select d from Doctor d join fetch d.specialty where d.active = true order by d.name")
    List<Doctor> findAllActive();

    @Query("select d from Doctor d join fetch d.specialty where d.active = true and d.specialty.id = :specialtyId order by d.name")
    List<Doctor> findActiveBySpecialty(@Param("specialtyId") Long specialtyId);

    @Query("select d from Doctor d join fetch d.specialty order by d.name")
    List<Doctor> findAllWithSpecialty();

    @Query("select d from Doctor d join fetch d.specialty where d.id = :id")
    Optional<Doctor> findByIdWithSpecialty(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Doctor d where d.id = :id")
    Optional<Doctor> findByIdForUpdate(@Param("id") Long id);

    boolean existsBySpecialtyId(Long specialtyId);
}