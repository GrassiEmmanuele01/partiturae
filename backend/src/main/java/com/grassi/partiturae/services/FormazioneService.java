package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.FormazioneRequest;
import com.grassi.partiturae.dto.FormazioneResponse;
import com.grassi.partiturae.dto.FileDownloadResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Formazione;
import com.grassi.partiturae.repositories.FormazioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FormazioneService {

    private final FormazioneRepository formazioneRepository;
    private final IscrizioneSocioService iscrizioneSocioService;
    private final MembroDirettivoService membroDirettivoService;

    public FormazioneService(FormazioneRepository formazioneRepository,
                         IscrizioneSocioService iscrizioneSocioService,
                         MembroDirettivoService membroDirettivoService) {
        this.formazioneRepository = formazioneRepository;
        this.iscrizioneSocioService = iscrizioneSocioService;
        this.membroDirettivoService = membroDirettivoService;
    }

    @Transactional(readOnly = true)
    public FormazioneResponse getFormazione() {
        return toResponse(findEntity());
    }

    @Transactional
    public FormazioneResponse createOrUpdate(FormazioneRequest request) {
        Formazione formazione = formazioneRepository.findAll().stream()
                .findFirst()
                .orElseGet(Formazione::new);

        formazione.setNome(request.getNome());
        formazione.setDescrizione(request.getDescrizione());
        formazione.setAnnoFondazione(request.getAnnoFondazione());
        formazione.setIndirizzo(request.getIndirizzo());
        formazione.setCodiceFiscale(request.getCodiceFiscale());
        formazione.setEmail(request.getEmail());
        formazione.setTelefono(request.getTelefono());
        formazione.setSitoWeb(request.getSitoWeb());

        return toResponse(formazioneRepository.save(formazione));
    }

    @Transactional
    public void uploadLogo(MultipartFile file) throws IOException {
        Formazione formazione = findEntity();
        formazione.setLogoNome(file.getOriginalFilename());
        formazione.setLogo(file.getBytes());
        formazioneRepository.save(formazione);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponse getLogo() {
        Formazione formazione = findEntity();

        if (formazione.getLogo() == null) {
            throw new ResourceNotFoundException("Nessun logo caricato per la formazione");
        }

        return new FileDownloadResponse(formazione.getLogoNome(), formazione.getLogo());
    }

    private Formazione findEntity() {
        return formazioneRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nessuna formazione configurata ancora"));
    }

    private FormazioneResponse toResponse(Formazione formazione) {
        return FormazioneResponse.builder()
                .id(formazione.getId())
                .nome(formazione.getNome())
                .descrizione(formazione.getDescrizione())
                .annoFondazione(formazione.getAnnoFondazione())
                .indirizzo(formazione.getIndirizzo())
                .codiceFiscale(formazione.getCodiceFiscale())
                .email(formazione.getEmail())
                .telefono(formazione.getTelefono())
                .sitoWeb(formazione.getSitoWeb())
                .numeroAssociatiAnnoCorrente(iscrizioneSocioService.countAssociatiAnnoCorrente())
                .direttivoInCarica(membroDirettivoService.getInCarica())
                .build();
    }
}