package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.IscrizioneSocioRequest;
import com.grassi.partiturae.dto.IscrizioneSocioResponse;
import com.grassi.partiturae.dto.IscrizioneSocioSummaryResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.IscrizioneSocio;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.repositories.IscrizioneSocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class IscrizioneSocioService {

    private final IscrizioneSocioRepository iscrizioneSocioRepository;
    private final SocioService socioService;

    public IscrizioneSocioService(IscrizioneSocioRepository iscrizioneSocioRepository, SocioService socioService) {
        this.iscrizioneSocioRepository = iscrizioneSocioRepository;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<IscrizioneSocioResponse> getBySocio(Long socioId) {
        return iscrizioneSocioRepository.findBySocioIdOrderByAnnoDesc(socioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IscrizioneSocioSummaryResponse getSummary(Long socioId) {
        List<IscrizioneSocio> tutte = iscrizioneSocioRepository.findBySocioIdOrderByAnnoDesc(socioId);
        int annoCorrente = Year.now().getValue();

        List<Integer> anniIscritto = tutte.stream()
                .filter(i -> Boolean.TRUE.equals(i.getIscritto()))
                .map(IscrizioneSocio::getAnno)
                .toList();

        return IscrizioneSocioSummaryResponse.builder()
                .iscrittoAnnoCorrente(anniIscritto.contains(annoCorrente))
                .anniIscritto(anniIscritto.size())
                .anni(anniIscritto)
                .build();
    }

    @Transactional
    public IscrizioneSocioResponse upsert(Long socioId, Integer anno, IscrizioneSocioRequest request) {
        Socio socio = socioService.findEntityById(socioId);

        IscrizioneSocio iscrizione = iscrizioneSocioRepository.findBySocioIdAndAnno(socioId, anno)
                .orElseGet(() -> IscrizioneSocio.builder().socio(socio).anno(anno).build());

        iscrizione.setIscritto(request.getIscritto());

        return toResponse(iscrizioneSocioRepository.save(iscrizione));
    }

    @Transactional
    public void delete(Long socioId, Integer anno) {
        IscrizioneSocio iscrizione = iscrizioneSocioRepository.findBySocioIdAndAnno(socioId, anno)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nessuna iscrizione per il socio " + socioId + " nell'anno " + anno));

        iscrizioneSocioRepository.delete(iscrizione);
    }

    @Transactional(readOnly = true)
    public int countAssociatiAnnoCorrente() {
        return iscrizioneSocioRepository.countByAnnoAndIscrittoTrue(Year.now().getValue());
    }

    private IscrizioneSocioResponse toResponse(IscrizioneSocio iscrizione) {
        return IscrizioneSocioResponse.builder()
                .anno(iscrizione.getAnno())
                .iscritto(iscrizione.getIscritto())
                .build();
    }
}