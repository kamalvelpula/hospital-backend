package com.careplus.hospital.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careplus.hospital.dto.SpecialtyResponse;
import com.careplus.hospital.repository.SpecialtyRepository;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll(Sort.by("name")).stream()
                .map(s -> new SpecialtyResponse(s.getId(), s.getName(), s.getDescription()))
                .toList();
    }
}