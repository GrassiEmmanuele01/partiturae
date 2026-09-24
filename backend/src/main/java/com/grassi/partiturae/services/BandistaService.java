package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.BandistaRequest;
import com.grassi.partiturae.dto.BandistaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Bandista;
import com.grassi.partiturae.repositories.BandistaRepository;

@Service
public class BandistaService {

    private final BandistaRepository bandistaRepository;

    public BandistaService(BandistaRepository bandistaRepository) {
        this.bandistaRepository = bandistaRepository;
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
        Bandista bandista = Bandista.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .mail(request.getMail())
                .codiceFiscale(request.getCodiceFiscale())
                .telefono(request.getTelefono())
                .build();

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public BandistaResponse update(Long id, BandistaRequest request) {
        Bandista bandista = findEntityById(id);

        bandista.setNome(request.getNome());
        bandista.setCognome(request.getCognome());
        bandista.setMail(request.getMail());
        bandista.setCodiceFiscale(request.getCodiceFiscale());
        bandista.setTelefono(request.getTelefono());

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public void delete(Long id) {
        Bandista bandista = findEntityById(id);
        bandistaRepository.delete(bandista);
    }

    private Bandista findEntityById(Long id) {
        return bandistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandista non trovato con id: " + id));
    }

    private BandistaResponse toResponse(Bandista bandista) {
        return BandistaResponse.builder()
                .id(bandista.getId())
                .nome(bandista.getNome())
                .cognome(bandista.getCognome())
                .mail(bandista.getMail())
                .codiceFiscale(bandista.getCodiceFiscale())
                .telefono(bandista.getTelefono())
                .build();
    }
}