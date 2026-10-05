package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.BandaRequest;
import com.grassi.partiturae.dto.BandaResponse;
import com.grassi.partiturae.dto.FileDownloadResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Banda;
import com.grassi.partiturae.repositories.BandaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BandaService {

    private final BandaRepository bandaRepository;
    private final IscrizioneSocioService iscrizioneSocioService;
    private final MembroDirettivoService membroDirettivoService;

    public BandaService(BandaRepository bandaRepository,
                         IscrizioneSocioService iscrizioneSocioService,
                         MembroDirettivoService membroDirettivoService) {
        this.bandaRepository = bandaRepository;
        this.iscrizioneSocioService = iscrizioneSocioService;
        this.membroDirettivoService = membroDirettivoService;
    }

    @Transactional(readOnly = true)
    public BandaResponse getBanda() {
        return toResponse(findEntity());
    }

    @Transactional
    public BandaResponse createOrUpdate(BandaRequest request) {
        Banda banda = bandaRepository.findAll().stream()
                .findFirst()
                .orElseGet(Banda::new);

        banda.setNome(request.getNome());
        banda.setDescrizione(request.getDescrizione());
        banda.setAnnoFondazione(request.getAnnoFondazione());
        banda.setIndirizzo(request.getIndirizzo());
        banda.setCodiceFiscale(request.getCodiceFiscale());
        banda.setEmail(request.getEmail());
        banda.setTelefono(request.getTelefono());
        banda.setSitoWeb(request.getSitoWeb());

        return toResponse(bandaRepository.save(banda));
    }

    @Transactional
    public void uploadLogo(MultipartFile file) throws IOException {
        Banda banda = findEntity();
        banda.setLogoNome(file.getOriginalFilename());
        banda.setLogo(file.getBytes());
        bandaRepository.save(banda);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponse getLogo() {
        Banda banda = findEntity();

        if (banda.getLogo() == null) {
            throw new ResourceNotFoundException("Nessun logo caricato per la banda");
        }

        return new FileDownloadResponse(banda.getLogoNome(), banda.getLogo());
    }

    private Banda findEntity() {
        return bandaRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nessuna banda configurata ancora"));
    }

    private BandaResponse toResponse(Banda banda) {
        return BandaResponse.builder()
                .id(banda.getId())
                .nome(banda.getNome())
                .descrizione(banda.getDescrizione())
                .annoFondazione(banda.getAnnoFondazione())
                .indirizzo(banda.getIndirizzo())
                .codiceFiscale(banda.getCodiceFiscale())
                .email(banda.getEmail())
                .telefono(banda.getTelefono())
                .sitoWeb(banda.getSitoWeb())
                .numeroAssociatiAnnoCorrente(iscrizioneSocioService.countAssociatiAnnoCorrente())
                .direttivoInCarica(membroDirettivoService.getInCarica())
                .build();
    }
}