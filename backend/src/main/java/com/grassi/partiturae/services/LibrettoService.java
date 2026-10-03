package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.LibrettoPartituraResponse;
import com.grassi.partiturae.dto.LibrettoRequest;
import com.grassi.partiturae.dto.LibrettoResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Libretto;
import com.grassi.partiturae.model.LibrettoPartitura;
import com.grassi.partiturae.model.Partitura;
import com.grassi.partiturae.repositories.LibrettoPartituraRepository;
import com.grassi.partiturae.repositories.LibrettoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class LibrettoService {

    private final LibrettoRepository librettoRepository;
    private final LibrettoPartituraRepository librettoPartituraRepository;
    private final PartituraService partituraService;

    public LibrettoService(LibrettoRepository librettoRepository,
                            LibrettoPartituraRepository librettoPartituraRepository,
                            PartituraService partituraService) {
        this.librettoRepository = librettoRepository;
        this.librettoPartituraRepository = librettoPartituraRepository;
        this.partituraService = partituraService;
    }

    @Transactional(readOnly = true)
    public List<LibrettoResponse> getAll() {
        return librettoRepository.findAll().stream()
                .map(l -> toResponse(l, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public LibrettoResponse getById(Long id) {
        return toResponse(findEntityById(id), true);
    }

    @Transactional
    public LibrettoResponse create(LibrettoRequest request) {
        Libretto libretto = Libretto.builder()
                .nome(request.getNome())
                .anno(request.getAnno())
                .descrizione(request.getDescrizione())
                .build();

        return toResponse(librettoRepository.save(libretto), false);
    }

    @Transactional
    public LibrettoResponse update(Long id, LibrettoRequest request) {
        Libretto libretto = findEntityById(id);
        libretto.setNome(request.getNome());
        libretto.setAnno(request.getAnno());
        libretto.setDescrizione(request.getDescrizione());

        return toResponse(librettoRepository.save(libretto), false);
    }

    @Transactional
    public void delete(Long id) {
        Libretto libretto = findEntityById(id);
        librettoRepository.delete(libretto);
    }

    @Transactional
    public LibrettoResponse addPartitura(Long librettoId, Long partituraId) {
        Libretto libretto = findEntityById(librettoId);
        Partitura partitura = partituraService.findEntityById(partituraId);

        if (librettoPartituraRepository.findByLibrettoIdAndPartituraId(librettoId, partituraId).isPresent()) {
            throw new IllegalStateException("Questa partitura è già presente nel libretto.");
        }

        int prossimoOrdine = librettoPartituraRepository.countByLibrettoId(librettoId) + 1;

        LibrettoPartitura lp = LibrettoPartitura.builder()
                .libretto(libretto)
                .partitura(partitura)
                .ordine(prossimoOrdine)
                .build();

        librettoPartituraRepository.save(lp);

        return toResponse(libretto, true);
    }

    @Transactional
    public LibrettoResponse removePartitura(Long librettoId, Long partituraId) {
        LibrettoPartitura lp = librettoPartituraRepository.findByLibrettoIdAndPartituraId(librettoId, partituraId)
                .orElseThrow(() -> new ResourceNotFoundException("Partitura non presente in questo libretto."));

        librettoPartituraRepository.delete(lp);

        return toResponse(findEntityById(librettoId), true);
    }

    @Transactional
    public LibrettoResponse reorder(Long librettoId, List<Long> partituraIdsInOrdine) {
        List<LibrettoPartitura> esistenti = librettoPartituraRepository.findByLibrettoIdOrderByOrdineAsc(librettoId);

        if (esistenti.size() != partituraIdsInOrdine.size()) {
            throw new IllegalArgumentException("L'elenco fornito non corrisponde alle partiture presenti nel libretto.");
        }

        Map<Long, LibrettoPartitura> byPartituraId = esistenti.stream()
                .collect(java.util.stream.Collectors.toMap(lp -> lp.getPartitura().getId(), lp -> lp));

        for (int i = 0; i < partituraIdsInOrdine.size(); i++) {
            Long partituraId = partituraIdsInOrdine.get(i);
            LibrettoPartitura lp = byPartituraId.get(partituraId);
            if (lp == null) {
                throw new IllegalArgumentException("La partitura con id " + partituraId + " non è presente in questo libretto.");
            }
            lp.setOrdine(i + 1);
        }

        librettoPartituraRepository.saveAll(esistenti);

        return toResponse(findEntityById(librettoId), true);
    }

    Libretto findEntityById(Long id) {
        return librettoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libretto non trovato con id: " + id));
    }

    private LibrettoResponse toResponse(Libretto libretto, boolean includePartiture) {
        List<LibrettoPartitura> righe = librettoPartituraRepository.findByLibrettoIdOrderByOrdineAsc(libretto.getId());

        LibrettoResponse.LibrettoResponseBuilder builder = LibrettoResponse.builder()
                .id(libretto.getId())
                .nome(libretto.getNome())
                .anno(libretto.getAnno())
                .descrizione(libretto.getDescrizione())
                .numeroPartiture(righe.size());

        if (includePartiture) {
            builder.partiture(righe.stream()
                    .map(lp -> LibrettoPartituraResponse.builder()
                            .ordine(lp.getOrdine())
                            .partitura(partituraService.toResponse(lp.getPartitura()))
                            .build())
                    .toList());
        }

        return builder.build();
    }
}