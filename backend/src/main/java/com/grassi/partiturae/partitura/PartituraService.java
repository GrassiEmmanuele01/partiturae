package com.grassi.partiturae.partitura;

import com.grassi.partiturae.autore.Autore;
import com.grassi.partiturae.autore.AutoreService;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartituraService {

    private final PartituraRepository partituraRepository;
    private final AutoreService autoreService;

    public PartituraService(PartituraRepository partituraRepository, AutoreService autoreService) {
        this.partituraRepository = partituraRepository;
        this.autoreService = autoreService;
    }

    @Transactional(readOnly = true)
    public List<PartituraResponse> getAll() {
        return partituraRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartituraResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public PartituraResponse create(PartituraRequest request) {
        Autore autore = autoreService.findEntityById(request.getAutoreId());

        Partitura partitura = Partitura.builder()
                .nome(request.getNome())
                .descrizione(request.getDescrizione())
                .anno(request.getAnno())
                .tipo(request.getTipo())
                .autore(autore)
                .build();

        return toResponse(partituraRepository.save(partitura));
    }

    @Transactional
    public PartituraResponse update(Long id, PartituraRequest request) {
        Partitura partitura = findEntityById(id);
        Autore autore = autoreService.findEntityById(request.getAutoreId());

        partitura.setNome(request.getNome());
        partitura.setDescrizione(request.getDescrizione());
        partitura.setAnno(request.getAnno());
        partitura.setTipo(request.getTipo());
        partitura.setAutore(autore);

        return toResponse(partituraRepository.save(partitura));
    }

    @Transactional
    public void delete(Long id) {
        Partitura partitura = findEntityById(id);
        partituraRepository.delete(partitura);
    }

    public Partitura findEntityById(Long id) {
        return partituraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partitura non trovata con id: " + id));
    }

    public PartituraResponse toResponse(Partitura partitura) {
        return PartituraResponse.builder()
                .id(partitura.getId())
                .nome(partitura.getNome())
                .descrizione(partitura.getDescrizione())
                .anno(partitura.getAnno())
                .tipo(partitura.getTipo())
                .autore(autoreService.toResponse(partitura.getAutore()))
                .build();
    }
}