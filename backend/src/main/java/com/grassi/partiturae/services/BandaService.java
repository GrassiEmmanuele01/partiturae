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

    public BandaService(BandaRepository bandaRepository) {
        this.bandaRepository = bandaRepository;
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
                .build();
    }
}