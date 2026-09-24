package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.PartituraRequest;
import com.grassi.partiturae.dto.PartituraResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Autore;
import com.grassi.partiturae.model.Partitura;
import com.grassi.partiturae.repositories.PartituraRepository;

@Service
public class PartituraService {

    private final PartituraRepository partituraRepository;
    private final AutoreService autoreService;

    public PartituraService(PartituraRepository partituraRepository, AutoreService autoreService) {
        this.partituraRepository = partituraRepository;
        this.autoreService = autoreService;
    }

    @Transactional(readOnly = true)
    public List<PartituraResponse> getAll() {
        return partituraRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartituraResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public PartituraResponse create(PartituraRequest request) {
        Autore autore = autoreService.findEntityById(request.getAutoreId());

        Partitura partitura = Partitura.builder()
                .nome(request.getNome())
                .descrizione(request.getDescrizione())
                .anno(request.getAnno())
                .autore(autore)
                .build();

        return toResponse(partituraRepository.save(partitura));
    }

    @Transactional
    public PartituraResponse update(Long id, PartituraRequest request) {
        Partitura partitura = findEntityById(id);
        Autore autore = autoreService.findEntityById(request.getAutoreId());

        partitura.setNome(request.getNome());
        partitura.setDescrizione(request.getDescrizione());
        partitura.setAnno(request.getAnno());
        partitura.setAutore(autore);

        return toResponse(partituraRepository.save(partitura));
    }

    @Transactional
    public void delete(Long id) {
        Partitura partitura = findEntityById(id);
        partituraRepository.delete(partitura);
    }

    Partitura findEntityById(Long id) {
        return partituraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partitura non trovata con id: " + id));
    }

    private PartituraResponse toResponse(Partitura partitura) {
        return PartituraResponse.builder()
                .id(partitura.getId())
                .nome(partitura.getNome())
                .descrizione(partitura.getDescrizione())
                .anno(partitura.getAnno())
                .autore(autoreService.toResponse(partitura.getAutore()))
                .build();
    }
}