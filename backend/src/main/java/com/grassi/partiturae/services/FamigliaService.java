package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.FamigliaRequest;
import com.grassi.partiturae.dto.FamigliaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Famiglia;
import com.grassi.partiturae.repositories.FamigliaRepository;

@Service
public class FamigliaService {

    private final FamigliaRepository famigliaRepository;

    public FamigliaService(FamigliaRepository famigliaRepository) {
        this.famigliaRepository = famigliaRepository;
    }

    @Transactional(readOnly = true)
    public List<FamigliaResponse> getAll() {
        return famigliaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FamigliaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public FamigliaResponse create(FamigliaRequest request) {
        Famiglia famiglia = Famiglia.builder()
                .nome(request.getNome())
                .build();

        return toResponse(famigliaRepository.save(famiglia));
    }

    @Transactional
    public FamigliaResponse update(Long id, FamigliaRequest request) {
        Famiglia famiglia = findEntityById(id);
        famiglia.setNome(request.getNome());
        return toResponse(famigliaRepository.save(famiglia));
    }

    @Transactional
    public void delete(Long id) {
        Famiglia famiglia = findEntityById(id);
        famigliaRepository.delete(famiglia);
    }

    Famiglia findEntityById(Long id) {
        return famigliaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Famiglia non trovata con id: " + id));
    }

    FamigliaResponse toResponse(Famiglia famiglia) {
        return FamigliaResponse.builder()
                .id(famiglia.getId())
                .nome(famiglia.getNome())
                .build();
    }
}