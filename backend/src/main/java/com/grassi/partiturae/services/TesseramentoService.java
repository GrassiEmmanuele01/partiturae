package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.TesseramentoRequest;
import com.grassi.partiturae.dto.TesseramentoResponse;
import com.grassi.partiturae.dto.TesseramentoSummaryResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Bandista;
import com.grassi.partiturae.model.Tesseramento;
import com.grassi.partiturae.repositories.TesseramentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class TesseramentoService {

    private final TesseramentoRepository tesseramentoRepository;
    private final BandistaService bandistaService;

    public TesseramentoService(TesseramentoRepository tesseramentoRepository, BandistaService bandistaService) {
        this.tesseramentoRepository = tesseramentoRepository;
        this.bandistaService = bandistaService;
    }

    @Transactional(readOnly = true)
    public List<TesseramentoResponse> getByBandista(Long bandistaId) {
        return tesseramentoRepository.findByBandistaIdOrderByAnnoDesc(bandistaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TesseramentoSummaryResponse getSummary(Long bandistaId) {
        List<Tesseramento> tutti = tesseramentoRepository.findByBandistaIdOrderByAnnoDesc(bandistaId);
        int annoCorrente = Year.now().getValue();

        List<Integer> anniTesserato = tutti.stream()
                .filter(t -> Boolean.TRUE.equals(t.getTesserato()))
                .map(Tesseramento::getAnno)
                .toList();

        boolean tesseratoQuestAnno = anniTesserato.contains(annoCorrente);

        return TesseramentoSummaryResponse.builder()
                .tesseratoAnnoCorrente(tesseratoQuestAnno)
                .anniTesserato(anniTesserato.size())
                .anni(anniTesserato)
                .build();
    }

    @Transactional
    public TesseramentoResponse upsert(Long bandistaId, Integer anno, TesseramentoRequest request) {
        Bandista bandista = bandistaService.findEntityById(bandistaId);

        Tesseramento tesseramento = tesseramentoRepository.findByBandistaIdAndAnno(bandistaId, anno)
                .orElseGet(() -> Tesseramento.builder().bandista(bandista).anno(anno).build());

        tesseramento.setTesserato(request.getTesserato());

        return toResponse(tesseramentoRepository.save(tesseramento));
    }

    @Transactional
    public void delete(Long bandistaId, Integer anno) {
        Tesseramento tesseramento = tesseramentoRepository.findByBandistaIdAndAnno(bandistaId, anno)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nessun tesseramento per il bandista " + bandistaId + " nell'anno " + anno));

        tesseramentoRepository.delete(tesseramento);
    }

    private TesseramentoResponse toResponse(Tesseramento tesseramento) {
        return TesseramentoResponse.builder()
                .anno(tesseramento.getAnno())
                .tesserato(tesseramento.getTesserato())
                .build();
    }
}