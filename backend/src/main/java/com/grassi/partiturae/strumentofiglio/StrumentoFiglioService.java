package com.grassi.partiturae.strumentofiglio;

import com.grassi.partiturae.common.dto.UtilizzoElemento;
import com.grassi.partiturae.common.dto.UtilizzoResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.parte.ParteRepository;
import com.grassi.partiturae.strumento.Strumento;
import com.grassi.partiturae.strumento.StrumentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StrumentoFiglioService {

    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final StrumentoService strumentoService;
    private final ParteRepository parteRepository;

    public StrumentoFiglioService(StrumentoFiglioRepository strumentoFiglioRepository,
                                   StrumentoService strumentoService,
                                   ParteRepository parteRepository) {
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.strumentoService = strumentoService;
        this.parteRepository = parteRepository;
    }

    @Transactional(readOnly = true)
    public List<StrumentoFiglioResponse> getAll() {
        return strumentoFiglioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StrumentoFiglioResponse> getByStrumento(Long strumentoId) {
        return strumentoFiglioRepository.findByStrumentoId(strumentoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StrumentoFiglioResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public StrumentoFiglioResponse create(StrumentoFiglioRequest request) {
        Strumento strumento = strumentoService.findEntityById(request.getStrumentoId());

        StrumentoFiglio strumentoFiglio = StrumentoFiglio.builder()
                .nome(request.getNome())
                .strumento(strumento)
                .build();

        return toResponse(strumentoFiglioRepository.save(strumentoFiglio));
    }

    @Transactional
    public StrumentoFiglioResponse update(Long id, StrumentoFiglioRequest request) {
        StrumentoFiglio strumentoFiglio = findEntityById(id);
        Strumento strumento = strumentoService.findEntityById(request.getStrumentoId());

        strumentoFiglio.setNome(request.getNome());
        strumentoFiglio.setStrumento(strumento);

        return toResponse(strumentoFiglioRepository.save(strumentoFiglio));
    }

    @Transactional
    public void delete(Long id) {
        StrumentoFiglio strumentoFiglio = findEntityById(id);
        strumentoFiglioRepository.delete(strumentoFiglio);
    }

    @Transactional(readOnly = true)
    public UtilizzoResponse getUtilizzo(Long id) {
        List<UtilizzoElemento> elementi = parteRepository.findAllByStrumentoFiglio(id).stream()
                .map(p -> new UtilizzoElemento("Parte", p.getPartitura().getNome()))
                .toList();

        return new UtilizzoResponse(elementi.size(), elementi);
    }

    public StrumentoFiglio findEntityById(Long id) {
        return strumentoFiglioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strumento (figlio) non trovato con id: " + id));
    }

    public StrumentoFiglioResponse toResponse(StrumentoFiglio strumentoFiglio) {
        return StrumentoFiglioResponse.builder()
                .id(strumentoFiglio.getId())
                .nome(strumentoFiglio.getNome())
                .strumentoId(strumentoFiglio.getStrumento().getId())
                .strumentoNome(strumentoFiglio.getStrumento().getNome())
                .build();
    }
}