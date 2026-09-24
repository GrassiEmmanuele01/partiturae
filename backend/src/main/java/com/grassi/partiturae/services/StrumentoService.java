package com.grassi.partiturae.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.StrumentoRequest;
import com.grassi.partiturae.dto.StrumentoResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Famiglia;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.StrumentoRepository;

@Service
public class StrumentoService {

    private final StrumentoRepository strumentoRepository;
    private final FamigliaService famigliaService;

    public StrumentoService(StrumentoRepository strumentoRepository, FamigliaService famigliaService) {
        this.strumentoRepository = strumentoRepository;
        this.famigliaService = famigliaService;
    }

    @Transactional(readOnly = true)
    public List<StrumentoResponse> getAll() {
        return strumentoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StrumentoResponse> getByFamiglia(Long famigliaId) {
        return strumentoRepository.findByFamigliaId(famigliaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StrumentoResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public StrumentoResponse create(StrumentoRequest request) {
        Famiglia famiglia = famigliaService.findEntityById(request.getFamigliaId());

        Strumento strumento = Strumento.builder()
                .nome(request.getNome())
                .famiglia(famiglia)
                .build();

        return toResponse(strumentoRepository.save(strumento));
    }

    @Transactional
    public StrumentoResponse update(Long id, StrumentoRequest request) {
        Strumento strumento = findEntityById(id);
        Famiglia famiglia = famigliaService.findEntityById(request.getFamigliaId());

        strumento.setNome(request.getNome());
        strumento.setFamiglia(famiglia);

        return toResponse(strumentoRepository.save(strumento));
    }

    @Transactional
    public void delete(Long id) {
        Strumento strumento = findEntityById(id);
        strumentoRepository.delete(strumento);
    }

    Strumento findEntityById(Long id) {
        return strumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strumento non trovato con id: " + id));
    }

    StrumentoResponse toResponse(Strumento strumento) {
        return StrumentoResponse.builder()
                .id(strumento.getId())
                .nome(strumento.getNome())
                .famigliaId(strumento.getFamiglia().getId())
                .famigliaNome(strumento.getFamiglia().getNome())
                .build();
    }
}