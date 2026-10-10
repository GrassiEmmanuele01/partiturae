package com.grassi.partiturae.parte;

import com.grassi.partiturae.common.dto.FileDownloadResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.musicista.Musicista;
import com.grassi.partiturae.profilo.ProfiloUtenteService;
import com.grassi.partiturae.strumento.Strumento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Le parti per chi suona: quelle degli strumenti del proprio profilo musicale.
 * Chi suona "Tromba" vede le parti di tutte le voci di tromba (Tromba 1, Tromba 2...) e sceglie la sua.
 */
@Service
public class MiePartiService {

    private final ProfiloUtenteService profiloUtenteService;
    private final ParteService parteService;

    public MiePartiService(ProfiloUtenteService profiloUtenteService, ParteService parteService) {
        this.profiloUtenteService = profiloUtenteService;
        this.parteService = parteService;
    }

    /** @param partituraId se indicato, solo le parti di quella partitura */
    @Transactional(readOnly = true)
    public MiePartiResponse mieParti(Long partituraId) {
        Optional<Musicista> musicista = profiloUtenteService.musicistaCorrente();

        if (musicista.isEmpty()) {
            return MiePartiResponse.builder()
                    .profiloTrovato(false)
                    .strumenti(List.of())
                    .parti(List.of())
                    .build();
        }

        Set<Strumento> strumenti = musicista.get().getStrumenti();

        List<String> nomi = strumenti.stream()
                .map(Strumento::getNome)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<Long> ids = strumenti.stream().map(Strumento::getId).toList();

        List<ParteResponse> parti = ids.isEmpty()
                ? List.of()
                : parteService.getByStrumenti(ids).stream()
                        .filter(parte -> partituraId == null || partituraId.equals(parte.getPartituraId()))
                        .toList();

        return MiePartiResponse.builder()
                .profiloTrovato(true)
                .strumenti(nomi)
                .parti(parti)
                .build();
    }

    /** Il PDF di una parte, solo se riguarda uno dei propri strumenti: altrimenti è come se non esistesse. */
    @Transactional(readOnly = true)
    public FileDownloadResponse pdf(Long parteId) {
        Set<Long> idsStrumenti = profiloUtenteService.musicistaCorrente()
                .map(Musicista::getStrumenti)
                .orElse(Set.of())
                .stream()
                .map(Strumento::getId)
                .collect(Collectors.toSet());

        Parte parte = parteService.findEntityById(parteId);

        boolean laMia = parte.getStrumenti().stream()
                .anyMatch(voce -> idsStrumenti.contains(voce.getStrumento().getId()));

        if (!laMia) {
            throw new ResourceNotFoundException("Parte non trovata con id: " + parteId);
        }

        return parteService.getPdf(parteId);
    }
}