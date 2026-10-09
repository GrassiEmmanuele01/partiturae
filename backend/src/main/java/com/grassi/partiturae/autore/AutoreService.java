package com.grassi.partiturae.autore;

import com.grassi.partiturae.common.dto.UtilizzoElemento;
import com.grassi.partiturae.common.dto.UtilizzoResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.partitura.PartituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoreService {

    private final AutoreRepository autoreRepository;
    private final PartituraRepository partituraRepository;

    public AutoreService(AutoreRepository autoreRepository, PartituraRepository partituraRepository) {
        this.autoreRepository = autoreRepository;
        this.partituraRepository = partituraRepository;
    }

    @Transactional(readOnly = true)
    public List<AutoreResponse> getAll() {
        return autoreRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutoreResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public AutoreResponse create(AutoreRequest request) {
        Autore autore = Autore.builder()
                .nominativo(request.getNominativo())
                .build();

        return toResponse(autoreRepository.save(autore));
    }

    @Transactional
    public AutoreResponse update(Long id, AutoreRequest request) {
        Autore autore = findEntityById(id);
        autore.setNominativo(request.getNominativo());
        return toResponse(autoreRepository.save(autore));
    }

    @Transactional
    public void delete(Long id) {
        Autore autore = findEntityById(id);
        autoreRepository.delete(autore);
    }

    @Transactional(readOnly = true)
    public UtilizzoResponse getUtilizzo(Long id) {
        List<UtilizzoElemento> elementi = partituraRepository.findByAutoreId(id).stream()
                .map(p -> new UtilizzoElemento("Partitura", p.getNome()))
                .toList();

        return new UtilizzoResponse(elementi.size(), elementi);
    }

    public Autore findEntityById(Long id) {
        return autoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autore non trovato con id: " + id));
    }

    public AutoreResponse toResponse(Autore autore) {
        return AutoreResponse.builder()
                .id(autore.getId())
                .nominativo(autore.getNominativo())
                .build();
    }
}