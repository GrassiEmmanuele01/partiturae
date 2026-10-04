package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.BandistaRequest;
import com.grassi.partiturae.dto.BandistaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Bandista;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.BandistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BandistaService {

    private final BandistaRepository bandistaRepository;
    private final StrumentoService strumentoService;
    private final SocioService socioService;

    public BandistaService(BandistaRepository bandistaRepository,
                            StrumentoService strumentoService,
                            SocioService socioService) {
        this.bandistaRepository = bandistaRepository;
        this.strumentoService = strumentoService;
        this.socioService = socioService;
    }

    @Transactional(readOnly = true)
    public List<BandistaResponse> getAll() {
        return bandistaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BandistaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public BandistaResponse create(BandistaRequest request) {
        Socio socio = socioService.findEntityById(request.getSocioId());

        Bandista bandista = Bandista.builder()
                .socio(socio)
                .build();

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public BandistaResponse update(Long id, BandistaRequest request) {
        Bandista bandista = findEntityById(id);
        Socio socio = socioService.findEntityById(request.getSocioId());
        bandista.setSocio(socio);

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public void delete(Long id) {
        Bandista bandista = findEntityById(id);
        bandistaRepository.delete(bandista);
    }

    @Transactional
    public BandistaResponse updateStrumenti(Long id, List<Long> strumentoIds) {
        Bandista bandista = findEntityById(id);

        Set<Strumento> strumenti = new HashSet<>();
        for (Long strumentoId : strumentoIds) {
            strumenti.add(strumentoService.findEntityById(strumentoId));
        }

        bandista.setStrumenti(strumenti);

        return toResponse(bandistaRepository.save(bandista));
    }

    Bandista findEntityById(Long id) {
        return bandistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandista non trovato con id: " + id));
    }

    private BandistaResponse toResponse(Bandista bandista) {
        return BandistaResponse.builder()
                .id(bandista.getId())
                .socio(socioService.toResponse(bandista.getSocio()))
                .strumenti(bandista.getStrumenti().stream()
                        .map(strumentoService::toResponse)
                        .toList())
                .build();
    }
}