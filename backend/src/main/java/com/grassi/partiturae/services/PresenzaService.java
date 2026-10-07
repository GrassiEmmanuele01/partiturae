package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.PresenzaRequest;
import com.grassi.partiturae.dto.PresenzaResponse;
import com.grassi.partiturae.model.Evento;
import com.grassi.partiturae.model.Presenza;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.repositories.PresenzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresenzaService {

    private final PresenzaRepository presenzaRepository;
    private final EventoService eventoService;
    private final SocioService socioService;

    public PresenzaService(PresenzaRepository presenzaRepository, EventoService eventoService, SocioService socioService) {
        this.presenzaRepository = presenzaRepository;
        this.eventoService = eventoService;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<PresenzaResponse> getByEvento(Long eventoId) {
        eventoService.findEntityById(eventoId); // valida che l'evento esista

        List<Socio> tuttiSoci = socioService.findAllEntities();
        Map<Long, Presenza> presenzePerSocio = presenzaRepository.findByEventoId(eventoId).stream()
                .collect(Collectors.toMap(p -> p.getSocio().getId(), p -> p));

        return tuttiSoci.stream()
                .map(socio -> {
                    Presenza p = presenzePerSocio.get(socio.getId());
                    boolean presente = p != null && Boolean.TRUE.equals(p.getPresente());

                    return PresenzaResponse.builder()
                            .socioId(socio.getId())
                            .nome(socio.getNome())
                            .cognome(socio.getCognome())
                            .presente(presente)
                            .build();
                })
                .toList();
    }

    @Transactional
    public PresenzaResponse upsert(Long eventoId, Long socioId, PresenzaRequest request) {
        Evento evento = eventoService.findEntityById(eventoId);
        Socio socio = socioService.findEntityById(socioId);

        Presenza presenza = presenzaRepository.findByEventoIdAndSocioId(eventoId, socioId)
                .orElseGet(() -> Presenza.builder().evento(evento).socio(socio).build());

        presenza.setPresente(request.getPresente());
        presenzaRepository.save(presenza);

        return PresenzaResponse.builder()
                .socioId(socio.getId())
                .nome(socio.getNome())
                .cognome(socio.getCognome())
                .presente(Boolean.TRUE.equals(presenza.getPresente()))
                .build();
    }
}