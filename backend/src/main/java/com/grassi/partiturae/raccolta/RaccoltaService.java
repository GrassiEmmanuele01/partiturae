package com.grassi.partiturae.raccolta;

import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.partitura.Partitura;
import com.grassi.partiturae.partitura.PartituraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RaccoltaService {

    private final RaccoltaRepository raccoltaRepository;
    private final RaccoltaPartituraRepository raccoltaPartituraRepository;
    private final PartituraService partituraService;

    public RaccoltaService(RaccoltaRepository raccoltaRepository,
                            RaccoltaPartituraRepository raccoltaPartituraRepository,
                            PartituraService partituraService) {
        this.raccoltaRepository = raccoltaRepository;
        this.raccoltaPartituraRepository = raccoltaPartituraRepository;
        this.partituraService = partituraService;
    }

    @Transactional(readOnly = true)
    public List<RaccoltaResponse> getAll() {
        return raccoltaRepository.findAll().stream()
                .map(l -> toResponse(l, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public RaccoltaResponse getById(Long id) {
        return toResponse(findEntityById(id), true);
    }

    @Transactional
    public RaccoltaResponse create(RaccoltaRequest request) {
        Raccolta raccolta = Raccolta.builder()
                .nome(request.getNome())
                .anno(request.getAnno())
                .descrizione(request.getDescrizione())
                .build();

        return toResponse(raccoltaRepository.save(raccolta), false);
    }

    @Transactional
    public RaccoltaResponse update(Long id, RaccoltaRequest request) {
        Raccolta raccolta = findEntityById(id);
        raccolta.setNome(request.getNome());
        raccolta.setAnno(request.getAnno());
        raccolta.setDescrizione(request.getDescrizione());

        return toResponse(raccoltaRepository.save(raccolta), false);
    }

    @Transactional
    public void delete(Long id) {
        Raccolta raccolta = findEntityById(id);
        raccoltaRepository.delete(raccolta);
    }

    @Transactional
    public RaccoltaResponse addPartitura(Long raccoltaId, Long partituraId) {
        Raccolta raccolta = findEntityById(raccoltaId);
        Partitura partitura = partituraService.findEntityById(partituraId);

        if (raccoltaPartituraRepository.findByRaccoltaIdAndPartituraId(raccoltaId, partituraId).isPresent()) {
            throw new IllegalStateException("Questa partitura è già presente nel raccolta.");
        }

        int prossimoOrdine = raccoltaPartituraRepository.countByRaccoltaId(raccoltaId) + 1;

        RaccoltaPartitura lp = RaccoltaPartitura.builder()
                .raccolta(raccolta)
                .partitura(partitura)
                .ordine(prossimoOrdine)
                .build();

        raccoltaPartituraRepository.save(lp);

        return toResponse(raccolta, true);
    }

    @Transactional
    public RaccoltaResponse removePartitura(Long raccoltaId, Long partituraId) {
        RaccoltaPartitura lp = raccoltaPartituraRepository.findByRaccoltaIdAndPartituraId(raccoltaId, partituraId)
                .orElseThrow(() -> new ResourceNotFoundException("Partitura non presente in questo raccolta."));

        raccoltaPartituraRepository.delete(lp);

        return toResponse(findEntityById(raccoltaId), true);
    }

    @Transactional
    public RaccoltaResponse reorder(Long raccoltaId, List<Long> partituraIdsInOrdine) {
        List<RaccoltaPartitura> esistenti = raccoltaPartituraRepository.findByRaccoltaIdOrderByOrdineAsc(raccoltaId);

        if (esistenti.size() != partituraIdsInOrdine.size()) {
            throw new IllegalArgumentException("L'elenco fornito non corrisponde alle partiture presenti nel raccolta.");
        }

        Map<Long, RaccoltaPartitura> byPartituraId = esistenti.stream()
                .collect(java.util.stream.Collectors.toMap(lp -> lp.getPartitura().getId(), lp -> lp));

        for (int i = 0; i < partituraIdsInOrdine.size(); i++) {
            Long partituraId = partituraIdsInOrdine.get(i);
            RaccoltaPartitura lp = byPartituraId.get(partituraId);
            if (lp == null) {
                throw new IllegalArgumentException("La partitura con id " + partituraId + " non è presente in questo raccolta.");
            }
            lp.setOrdine(i + 1);
        }

        raccoltaPartituraRepository.saveAll(esistenti);

        return toResponse(findEntityById(raccoltaId), true);
    }

    public Raccolta findEntityById(Long id) {
        return raccoltaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raccolta non trovato con id: " + id));
    }

    private RaccoltaResponse toResponse(Raccolta raccolta, boolean includePartiture) {
        List<RaccoltaPartitura> righe = raccoltaPartituraRepository.findByRaccoltaIdOrderByOrdineAsc(raccolta.getId());

        RaccoltaResponse.RaccoltaResponseBuilder builder = RaccoltaResponse.builder()
                .id(raccolta.getId())
                .nome(raccolta.getNome())
                .anno(raccolta.getAnno())
                .descrizione(raccolta.getDescrizione())
                .numeroPartiture(righe.size());

        if (includePartiture) {
            builder.partiture(righe.stream()
                    .map(lp -> RaccoltaPartituraResponse.builder()
                            .ordine(lp.getOrdine())
                            .partitura(partituraService.toResponse(lp.getPartitura()))
                            .build())
                    .toList());
        }

        return builder.build();
    }
}