package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.SocioRequest;
import com.grassi.partiturae.dto.SocioResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Socio;
import com.grassi.partiturae.repositories.IscrizioneSocioRepository;
import com.grassi.partiturae.repositories.SocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class SocioService {

    private final SocioRepository socioRepository;
    private final IscrizioneSocioRepository iscrizioneSocioRepository;

    public SocioService(SocioRepository socioRepository, IscrizioneSocioRepository iscrizioneSocioRepository) {
        this.socioRepository = socioRepository;
        this.iscrizioneSocioRepository = iscrizioneSocioRepository;
    }

    @Transactional(readOnly = true)
    public List<SocioResponse> getAll() {
        return socioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SocioResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public SocioResponse create(SocioRequest request) {
        Socio socio = Socio.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .mail(request.getMail())
                .codiceFiscale(request.getCodiceFiscale())
                .telefono(request.getTelefono())
                .aggiunto(Boolean.TRUE.equals(request.getAggiunto()))
                .build();

        return toResponse(socioRepository.save(socio));
    }

    @Transactional
    public SocioResponse update(Long id, SocioRequest request) {
        Socio socio = findEntityById(id);

        socio.setNome(request.getNome());
        socio.setCognome(request.getCognome());
        socio.setMail(request.getMail());
        socio.setCodiceFiscale(request.getCodiceFiscale());
        socio.setTelefono(request.getTelefono());
        socio.setAggiunto(Boolean.TRUE.equals(request.getAggiunto()));

        return toResponse(socioRepository.save(socio));
    }

    @Transactional
    public void delete(Long id) {
        Socio socio = findEntityById(id);
        socioRepository.delete(socio);
    }

    Socio findEntityById(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio non trovato con id: " + id));
    }

    SocioResponse toResponse(Socio socio) {
        int annoCorrente = Year.now().getValue();
        boolean iscritto = iscrizioneSocioRepository.findBySocioIdAndAnno(socio.getId(), annoCorrente)
                .map(i -> Boolean.TRUE.equals(i.getIscritto()))
                .orElse(false);

        return SocioResponse.builder()
                .id(socio.getId())
                .nome(socio.getNome())
                .cognome(socio.getCognome())
                .mail(socio.getMail())
                .codiceFiscale(socio.getCodiceFiscale())
                .telefono(socio.getTelefono())
                .aggiunto(Boolean.TRUE.equals(socio.getAggiunto()))
                .iscrittoAnnoCorrente(iscritto)
                .build();
    }
}