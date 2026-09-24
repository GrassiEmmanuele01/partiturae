package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.ParteRequest;
import com.grassi.partiturae.dto.ParteResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Parte;
import com.grassi.partiturae.model.Partitura;
import com.grassi.partiturae.model.StrumentoFiglio;
import com.grassi.partiturae.repositories.ParteRepository;
import com.grassi.partiturae.repositories.StrumentoFiglioRepository;

@Service
public class ParteService {

    private final ParteRepository parteRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final PartituraService partituraService;

    public ParteService(ParteRepository parteRepository,
                         StrumentoFiglioRepository strumentoFiglioRepository,
                         PartituraService partituraService) {
        this.parteRepository = parteRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.partituraService = partituraService;
    }

    @Transactional(readOnly = true)
    public List<ParteResponse> getAll() {
        return parteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParteResponse> getByPartitura(Long partituraId) {
        return parteRepository.findByPartituraId(partituraId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParteResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ParteResponse create(ParteRequest request) {
        Partitura partitura = partituraService.findEntityById(request.getPartituraId());
        StrumentoFiglio strumentoFiglio = findStrumentoFiglioById(request.getStrumentoFiglioId());

        Parte parte = Parte.builder()
                .nome(request.getNome())
                .partitura(partitura)
                .strumentoFiglio(strumentoFiglio)
                .libretto(request.getLibretto())
                .build();

        return toResponse(parteRepository.save(parte));
    }

    @Transactional
    public ParteResponse update(Long id, ParteRequest request) {
        Parte parte = findEntityById(id);
        Partitura partitura = partituraService.findEntityById(request.getPartituraId());
        StrumentoFiglio strumentoFiglio = findStrumentoFiglioById(request.getStrumentoFiglioId());

        parte.setNome(request.getNome());
        parte.setPartitura(partitura);
        parte.setStrumentoFiglio(strumentoFiglio);
        parte.setLibretto(request.getLibretto());

        return toResponse(parteRepository.save(parte));
    }

    @Transactional
    public void delete(Long id) {
        Parte parte = findEntityById(id);
        parteRepository.delete(parte);
    }

    Parte findEntityById(Long id) {
        return parteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parte non trovata con id: " + id));
    }

    private StrumentoFiglio findStrumentoFiglioById(Long id) {
        return strumentoFiglioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strumento (figlio) non trovato con id: " + id));
    }

    private ParteResponse toResponse(Parte parte) {
        return ParteResponse.builder()
                .id(parte.getId())
                .nome(parte.getNome())
                .libretto(parte.getLibretto())
                .pdfNome(parte.getPdfNome())
                .partituraId(parte.getPartitura().getId())
                .partituraNome(parte.getPartitura().getNome())
                .strumentoFiglioId(parte.getStrumentoFiglio().getId())
                .strumentoFiglioNome(parte.getStrumentoFiglio().getNome())
                .build();
    }
}