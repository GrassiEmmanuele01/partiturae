package com.grassi.partiturae.direttivo;

import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.socio.Socio;
import com.grassi.partiturae.socio.SocioService;
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
        verificaCaricaUnica(request.getCarica(), request.getAnnoInizio(), request.getAnnoFine(), null);

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
        verificaCaricaUnica(request.getCarica(), request.getAnnoInizio(), request.getAnnoFine(), id);

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

    private void verificaCaricaUnica(CaricaDirettivo carica, Integer annoInizio, Integer annoFine, Long idEscluso) {
        if (!carica.isUnica()) {
            return;
        }

        boolean conflitto = membroDirettivoRepository.findAll().stream()
                .filter(m -> m.getCarica() == carica)
                .filter(m -> idEscluso == null || !m.getId().equals(idEscluso))
                .anyMatch(m -> mandatiSovrapposti(annoInizio, annoFine, m.getAnnoInizio(), m.getAnnoFine()));

        if (conflitto) {
            throw new IllegalStateException(
                    "La carica di " + formatCarica(carica) + " è già assegnata a qualcun altro in questo periodo.");
        }
    }

    private boolean mandatiSovrapposti(Integer inizio1, Integer fine1, Integer inizio2, Integer fine2) {
        boolean iniziaPrimaCheLaltroFinisca = fine2 == null || inizio1 <= fine2;
        boolean laltroIniziaPrimaCheFinisca = fine1 == null || inizio2 <= fine1;
        return iniziaPrimaCheLaltroFinisca && laltroIniziaPrimaCheFinisca;
    }

    private String formatCarica(CaricaDirettivo carica) {
        return carica.name().charAt(0) + carica.name().substring(1).toLowerCase().replace('_', ' ');
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