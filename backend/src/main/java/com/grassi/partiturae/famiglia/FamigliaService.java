package com.grassi.partiturae.famiglia;

import com.grassi.partiturae.common.dto.UtilizzoElemento;
import com.grassi.partiturae.common.dto.UtilizzoResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.strumento.StrumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FamigliaService {

    private final FamigliaRepository famigliaRepository;
    private final StrumentoRepository strumentoRepository;

    public FamigliaService(FamigliaRepository famigliaRepository, StrumentoRepository strumentoRepository) {
        this.famigliaRepository = famigliaRepository;
        this.strumentoRepository = strumentoRepository;
    }

    @Transactional(readOnly = true)
    public List<FamigliaResponse> getAll() {
        return famigliaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FamigliaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public FamigliaResponse create(FamigliaRequest request) {
        Famiglia famiglia = Famiglia.builder()
                .nome(request.getNome())
                .build();

        return toResponse(famigliaRepository.save(famiglia));
    }

    @Transactional
    public FamigliaResponse update(Long id, FamigliaRequest request) {
        Famiglia famiglia = findEntityById(id);
        famiglia.setNome(request.getNome());
        return toResponse(famigliaRepository.save(famiglia));
    }

    @Transactional
    public void delete(Long id) {
        Famiglia famiglia = findEntityById(id);
        famigliaRepository.delete(famiglia);
    }

    @Transactional(readOnly = true)
    public UtilizzoResponse getUtilizzo(Long id) {
        List<UtilizzoElemento> elementi = strumentoRepository.findByFamigliaId(id).stream()
                .map(s -> new UtilizzoElemento("Strumento", s.getNome()))
                .toList();

        return new UtilizzoResponse(elementi.size(), elementi);
    }

    public Famiglia findEntityById(Long id) {
        return famigliaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Famiglia non trovata con id: " + id));
    }

    public FamigliaResponse toResponse(Famiglia famiglia) {
        return FamigliaResponse.builder()
                .id(famiglia.getId())
                .nome(famiglia.getNome())
                .build();
    }
}