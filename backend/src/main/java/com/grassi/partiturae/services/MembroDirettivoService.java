package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.MembroDirettivoRequest;
import com.grassi.partiturae.dto.MembroDirettivoResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.MembroDirettivo;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.repositories.MembroDirettivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class MembroDirettivoService {

    private final MembroDirettivoRepository membroDirettivoRepository;
    private final SocioService socioService;

    public MembroDirettivoService(MembroDirettivoRepository membroDirettivoRepository, SocioService socioService) {
        this.membroDirettivoRepository = membroDirettivoRepository;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<MembroDirettivoResponse> getAll() {
        return membroDirettivoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembroDirettivoResponse> getInCarica() {
        int annoCorrente = Year.now().getValue();
        return membroDirettivoRepository.findAll().stream()
                .filter(m -> m.getAnnoFine() == null || m.getAnnoFine() >= annoCorrente)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MembroDirettivoResponse create(MembroDirettivoRequest request) {
        Socio socio = socioService.findEntityById(request.getSocioId());

        MembroDirettivo membro = MembroDirettivo.builder()
                .socio(socio)
                .carica(request.getCarica())
                .annoInizio(request.getAnnoInizio())
                .annoFine(request.getAnnoFine())
                .build();

        return toResponse(membroDirettivoRepository.save(membro));
    }

    @Transactional
    public MembroDirettivoResponse update(Long id, MembroDirettivoRequest request) {
        MembroDirettivo membro = findEntityById(id);
        Socio socio = socioService.findEntityById(request.getSocioId());

        membro.setSocio(socio);
        membro.setCarica(request.getCarica());
        membro.setAnnoInizio(request.getAnnoInizio());
        membro.setAnnoFine(request.getAnnoFine());

        return toResponse(membroDirettivoRepository.save(membro));
    }

    @Transactional
    public void delete(Long id) {
        MembroDirettivo membro = findEntityById(id);
        membroDirettivoRepository.delete(membro);
    }

    private MembroDirettivo findEntityById(Long id) {
        return membroDirettivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro del direttivo non trovato con id: " + id));
    }

    private MembroDirettivoResponse toResponse(MembroDirettivo membro) {
        int annoCorrente = Year.now().getValue();
        boolean inCarica = membro.getAnnoFine() == null || membro.getAnnoFine() >= annoCorrente;

        return MembroDirettivoResponse.builder()
                .id(membro.getId())
                .socio(socioService.toResponse(membro.getSocio()))
                .carica(membro.getCarica())
                .annoInizio(membro.getAnnoInizio())
                .annoFine(membro.getAnnoFine())
                .inCarica(inCarica)
                .build();
    }
}