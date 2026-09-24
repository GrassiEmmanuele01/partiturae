package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.AutoreRequest;
import com.grassi.partiturae.dto.AutoreResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Autore;
import com.grassi.partiturae.repositories.AutoreRepository;

@Service
public class AutoreService {

    private final AutoreRepository autoreRepository;

    public AutoreService(AutoreRepository autoreRepository) {
        this.autoreRepository = autoreRepository;
    }

    @Transactional(readOnly = true)
    public List<AutoreResponse> getAll() {
        return autoreRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutoreResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public AutoreResponse create(AutoreRequest request) {
        Autore autore = Autore.builder()
                .nominativo(request.getNominativo())
                .build();

        return toResponse(autoreRepository.save(autore));
    }

    @Transactional
    public AutoreResponse update(Long id, AutoreRequest request) {
        Autore autore = findEntityById(id);
        autore.setNominativo(request.getNominativo());
        return toResponse(autoreRepository.save(autore));
    }

    @Transactional
    public void delete(Long id) {
        Autore autore = findEntityById(id);
        autoreRepository.delete(autore);
    }

    Autore findEntityById(Long id) {
        return autoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autore non trovato con id: " + id));
    }

    AutoreResponse toResponse(Autore autore) {
        return AutoreResponse.builder()
                .id(autore.getId())
                .nominativo(autore.getNominativo())
                .build();
    }
}