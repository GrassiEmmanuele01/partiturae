package com.grassi.partiturae.parte;

import com.grassi.partiturae.common.dto.FileDownloadResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.partitura.Partitura;
import com.grassi.partiturae.partitura.PartituraService;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioRepository;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioResponse;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ParteService {

    private final ParteRepository parteRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final StrumentoFiglioService strumentoFiglioService;
    private final PartituraService partituraService;

    public ParteService(ParteRepository parteRepository,
                         StrumentoFiglioRepository strumentoFiglioRepository,
                         StrumentoFiglioService strumentoFiglioService,
                         PartituraService partituraService) {
        this.parteRepository = parteRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.strumentoFiglioService = strumentoFiglioService;
        this.partituraService = partituraService;
    }

    @Transactional(readOnly = true)
    public List<ParteResponse> getByPartitura(Long partituraId) {
        return toSortedResponses(parteRepository.findAllByPartitura(partituraId));
    }

    @Transactional(readOnly = true)
    public List<ParteResponse> getByStrumentoFiglio(Long strumentoFiglioId) {
        return toSortedResponses(parteRepository.findAllByStrumentoFiglio(strumentoFiglioId));
    }

    @Transactional(readOnly = true)
    public List<ParteResponse> getByStrumento(Long strumentoId) {
        return toSortedResponses(parteRepository.findAllByStrumento(strumentoId));
    }

    @Transactional
    public ParteResponse create(ParteRequest request) {
        Partitura partitura = partituraService.findEntityById(request.getPartituraId());

        Parte parte = Parte.builder()
                .partitura(partitura)
                .strumenti(loadStrumenti(request.getStrumentoFiglioIds()))
                .build();

        return toResponse(parteRepository.save(parte));
    }

    @Transactional
    public ParteResponse updateStrumenti(Long id, List<Long> strumentoFiglioIds) {
        Parte parte = findEntityById(id);
        Set<StrumentoFiglio> nuovi = loadStrumenti(strumentoFiglioIds);

        parte.getStrumenti().clear();
        parte.getStrumenti().addAll(nuovi);

        return toResponse(parteRepository.save(parte));
    }

    @Transactional
    public void delete(Long id) {
        Parte parte = findEntityById(id);
        parteRepository.delete(parte);
    }

    @Transactional
    public void uploadPdf(Long id, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file è vuoto.");
        }

        String originale = file.getOriginalFilename();
        boolean sembraPdf = "application/pdf".equals(file.getContentType())
                || (originale != null && originale.toLowerCase().endsWith(".pdf"));
        if (!sembraPdf) {
            throw new IllegalArgumentException("Il file deve essere un PDF.");
        }

        Parte parte = findEntityById(id);

        if (parte.getDocumento() == null) {
            parte.setDocumento(new ParteDocumento());
        }
        parte.getDocumento().setContenuto(file.getBytes());

        parteRepository.save(parte);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponse getPdf(Long id) {
        Parte parte = findEntityById(id);

        if (parte.getDocumento() == null) {
            throw new ResourceNotFoundException("Nessun PDF caricato per questa parte.");
        }

        return new FileDownloadResponse(nomeFile(parte), parte.getDocumento().getContenuto());
    }

    public Parte findEntityById(Long id) {
        return parteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parte non trovata con id: " + id));
    }

    private Set<StrumentoFiglio> loadStrumenti(List<Long> ids) {
        Set<Long> distinti = new HashSet<>(ids);
        List<StrumentoFiglio> trovati = strumentoFiglioRepository.findAllById(distinti);

        if (trovati.size() != distinti.size()) {
            throw new ResourceNotFoundException("Uno o più strumenti selezionati non esistono.");
        }

        return new HashSet<>(trovati);
    }

    private List<ParteResponse> toSortedResponses(List<Parte> parti) {
        return parti.stream()
                .sorted(Comparator.comparing((Parte p) -> p.getPartitura().getNome(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Parte::getId))
                .map(this::toResponse)
                .toList();
    }

    private String nomeFile(Parte parte) {
        List<String> nomiStrumenti = parte.getStrumenti().stream()
                .map(StrumentoFiglio::getNome)
                .toList();

        return PdfFileNames.forParte(parte.getPartitura().getNome(), nomiStrumenti);
    }

    private ParteResponse toResponse(Parte parte) {
        List<StrumentoFiglioResponse> strumenti = parte.getStrumenti().stream()
                .map(strumentoFiglioService::toResponse)
                .sorted(Comparator.comparing(StrumentoFiglioResponse::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();

        boolean pdfPresente = parte.getDocumento() != null;

        return ParteResponse.builder()
                .id(parte.getId())
                .partituraId(parte.getPartitura().getId())
                .partituraNome(parte.getPartitura().getNome())
                .strumenti(strumenti)
                .pdfPresente(pdfPresente)
                .pdfNome(pdfPresente ? nomeFile(parte) : null)
                .build();
    }
}
