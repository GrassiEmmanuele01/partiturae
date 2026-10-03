package com.grassi.partiturae.services;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grassi.partiturae.dto.BandistaRequest;
import com.grassi.partiturae.dto.BandistaResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Bandista;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.repositories.BandistaRepository;
import com.grassi.partiturae.repositories.TesseramentoRepository;

@Service
public class BandistaService {

    private final BandistaRepository bandistaRepository;
    private final TesseramentoRepository tesseramentoRepository;
    private final StrumentoService strumentoService;

    public BandistaService(BandistaRepository bandistaRepository,
                            TesseramentoRepository tesseramentoRepository,
                            StrumentoService strumentoService) {
        this.bandistaRepository = bandistaRepository;
        this.tesseramentoRepository = tesseramentoRepository;
        this.strumentoService = strumentoService;
    }

    @Transactional(readOnly = true)
    public List<BandistaResponse> getAll() {
        return bandistaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BandistaResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public BandistaResponse create(BandistaRequest request) {
        Bandista bandista = Bandista.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .mail(request.getMail())
                .codiceFiscale(request.getCodiceFiscale())
                .telefono(request.getTelefono())
                .build();

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public BandistaResponse update(Long id, BandistaRequest request) {
        Bandista bandista = findEntityById(id);

        bandista.setNome(request.getNome());
        bandista.setCognome(request.getCognome());
        bandista.setMail(request.getMail());
        bandista.setCodiceFiscale(request.getCodiceFiscale());
        bandista.setTelefono(request.getTelefono());

        return toResponse(bandistaRepository.save(bandista));
    }

    @Transactional
    public void delete(Long id) {
        Bandista bandista = findEntityById(id);
        bandistaRepository.delete(bandista);
    }

    @Transactional
    public BandistaResponse updateStrumenti(Long id, List<Long> strumentoIds) {
        Bandista bandista = findEntityById(id);

        Set<Strumento> strumenti = new HashSet<>();
        for (Long strumentoId : strumentoIds) {
            strumenti.add(strumentoService.findEntityById(strumentoId));
        }

        bandista.setStrumenti(strumenti);

        return toResponse(bandistaRepository.save(bandista));
    }

    Bandista findEntityById(Long id) {
        return bandistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bandista non trovato con id: " + id));
    }

    private BandistaResponse toResponse(Bandista bandista) {
        int annoCorrente = Year.now().getValue();
        boolean tesserato = tesseramentoRepository.findByBandistaIdAndAnno(bandista.getId(), annoCorrente)
                .map(t -> Boolean.TRUE.equals(t.getTesserato()))
                .orElse(false);

        return BandistaResponse.builder()
                .id(bandista.getId())
                .nome(bandista.getNome())
                .cognome(bandista.getCognome())
                .mail(bandista.getMail())
                .codiceFiscale(bandista.getCodiceFiscale())
                .telefono(bandista.getTelefono())
                .strumenti(bandista.getStrumenti().stream()
                        .map(strumentoService::toResponse)
                        .toList())
                .tesseratoAnnoCorrente(tesserato)
                .build();
    }
}