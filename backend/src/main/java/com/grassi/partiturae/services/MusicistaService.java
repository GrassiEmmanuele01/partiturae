package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.MusicistaRequest;
import com.grassi.partiturae.dto.MusicistaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Musicista;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.MusicistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MusicistaService {

    private final MusicistaRepository musicistaRepository;
    private final StrumentoService strumentoService;
    private final SocioService socioService;

    public MusicistaService(MusicistaRepository musicistaRepository,
                            StrumentoService strumentoService,
                            SocioService socioService) {
        this.musicistaRepository = musicistaRepository;
        this.strumentoService = strumentoService;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<MusicistaResponse> getAll() {
        return musicistaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MusicistaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public MusicistaResponse create(MusicistaRequest request) {
        Socio socio = socioService.findEntityById(request.getSocioId());

        Musicista musicista = Musicista.builder()
                .socio(socio)
                .build();

        return toResponse(musicistaRepository.save(musicista));
    }

    @Transactional
    public MusicistaResponse update(Long id, MusicistaRequest request) {
        Musicista musicista = findEntityById(id);
        Socio socio = socioService.findEntityById(request.getSocioId());
        musicista.setSocio(socio);

        return toResponse(musicistaRepository.save(musicista));
    }

    @Transactional
    public void delete(Long id) {
        Musicista musicista = findEntityById(id);
        musicistaRepository.delete(musicista);
    }

    @Transactional
    public MusicistaResponse updateStrumenti(Long id, List<Long> strumentoIds) {
        Musicista musicista = findEntityById(id);

        Set<Strumento> strumenti = new HashSet<>();
        for (Long strumentoId : strumentoIds) {
            strumenti.add(strumentoService.findEntityById(strumentoId));
        }

        musicista.setStrumenti(strumenti);

        return toResponse(musicistaRepository.save(musicista));
    }

    Musicista findEntityById(Long id) {
        return musicistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Musicista non trovato con id: " + id));
    }

    private MusicistaResponse toResponse(Musicista musicista) {
        return MusicistaResponse.builder()
                .id(musicista.getId())
                .socio(socioService.toResponse(musicista.getSocio()))
                .strumenti(musicista.getStrumenti().stream()
                        .map(strumentoService::toResponse)
                        .toList())
                .build();
    }
}