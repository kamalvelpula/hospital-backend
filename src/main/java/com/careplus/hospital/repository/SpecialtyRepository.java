package com.careplus.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careplus.hospital.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}