package com.grassi.partiturae.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.BandaRequest;
import com.grassi.partiturae.dto.BandaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Banda;
import com.grassi.partiturae.repositories.BandaRepository;

@Service
public class BandaService {

    private final BandaRepository bandaRepository;

    public BandaService(BandaRepository bandaRepository) {
        this.bandaRepository = bandaRepository;
    }

    @Transactional(readOnly = true)
    public BandaResponse getBanda() {
        Banda banda = bandaRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nessuna banda configurata ancora"));

        return toResponse(banda);
    }

    @Transactional
    public BandaResponse createOrUpdate(BandaRequest request) {
        Banda banda = bandaRepository.findAll().stream()
                .findFirst()
                .orElseGet(Banda::new);

        banda.setNome(request.getNome());
        banda.setDescrizione(request.getDescrizione());

        return toResponse(bandaRepository.save(banda));
    }

    private BandaResponse toResponse(Banda banda) {
        return BandaResponse.builder()
                .id(banda.getId())
                .nome(banda.getNome())
                .descrizione(banda.getDescrizione())
                .build();
    }
}