package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.StrumentoRequest;
import com.grassi.partiturae.dto.StrumentoResponse;
import com.grassi.partiturae.dto.UtilizzoElemento;
import com.grassi.partiturae.dto.UtilizzoResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Famiglia;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.MusicistaRepository;
import com.grassi.partiturae.repositories.StrumentoFiglioRepository;
import com.grassi.partiturae.repositories.StrumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StrumentoService {

    private final StrumentoRepository strumentoRepository;
    private final FamigliaService famigliaService;
    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final MusicistaRepository musicistaRepository;

    public StrumentoService(StrumentoRepository strumentoRepository,
                             FamigliaService famigliaService,
                             StrumentoFiglioRepository strumentoFiglioRepository,
                             MusicistaRepository musicistaRepository) {
        this.strumentoRepository = strumentoRepository;
        this.famigliaService = famigliaService;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.musicistaRepository = musicistaRepository;
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

    @Transactional(readOnly = true)
    public UtilizzoResponse getUtilizzo(Long id) {
        List<UtilizzoElemento> elementi = new ArrayList<>();

        strumentoFiglioRepository.findByStrumentoId(id)
                .forEach(sf -> elementi.add(new UtilizzoElemento("Parte strumento", sf.getNome())));

        musicistaRepository.findByStrumentiId(id)
                .forEach(m -> elementi.add(new UtilizzoElemento("Musicista", m.getSocio().getNome() + " " + m.getSocio().getCognome())));

        return new UtilizzoResponse(elementi.size(), elementi);
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