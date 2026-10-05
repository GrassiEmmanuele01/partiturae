package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.StrumentistaRequest;
import com.grassi.partiturae.dto.StrumentistaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Strumentista;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.StrumentistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StrumentistaService {

    private final StrumentistaRepository strumentistaRepository;
    private final StrumentoService strumentoService;
    private final SocioService socioService;

    public StrumentistaService(StrumentistaRepository strumentistaRepository,
                            StrumentoService strumentoService,
                            SocioService socioService) {
        this.strumentistaRepository = strumentistaRepository;
        this.strumentoService = strumentoService;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<StrumentistaResponse> getAll() {
        return strumentistaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StrumentistaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public StrumentistaResponse create(StrumentistaRequest request) {
        Socio socio = socioService.findEntityById(request.getSocioId());

        Strumentista strumentista = Strumentista.builder()
                .socio(socio)
                .build();

        return toResponse(strumentistaRepository.save(strumentista));
    }

    @Transactional
    public StrumentistaResponse update(Long id, StrumentistaRequest request) {
        Strumentista strumentista = findEntityById(id);
        Socio socio = socioService.findEntityById(request.getSocioId());
        strumentista.setSocio(socio);

        return toResponse(strumentistaRepository.save(strumentista));
    }

    @Transactional
    public void delete(Long id) {
        Strumentista strumentista = findEntityById(id);
        strumentistaRepository.delete(strumentista);
    }

    @Transactional
    public StrumentistaResponse updateStrumenti(Long id, List<Long> strumentoIds) {
        Strumentista strumentista = findEntityById(id);

        Set<Strumento> strumenti = new HashSet<>();
        for (Long strumentoId : strumentoIds) {
            strumenti.add(strumentoService.findEntityById(strumentoId));
        }

        strumentista.setStrumenti(strumenti);

        return toResponse(strumentistaRepository.save(strumentista));
    }

    Strumentista findEntityById(Long id) {
        return strumentistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strumentista non trovato con id: " + id));
    }

    private StrumentistaResponse toResponse(Strumentista strumentista) {
        return StrumentistaResponse.builder()
                .id(strumentista.getId())
                .socio(socioService.toResponse(strumentista.getSocio()))
                .strumenti(strumentista.getStrumenti().stream()
                        .map(strumentoService::toResponse)
                        .toList())
                .build();
    }
}