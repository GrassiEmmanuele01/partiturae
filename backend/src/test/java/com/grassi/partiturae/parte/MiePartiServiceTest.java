package com.grassi.partiturae.parte;

import com.grassi.partiturae.common.dto.FileDownloadResponse;
import com.grassi.partiturae.common.exception.ResourceNotFoundException;
import com.grassi.partiturae.musicista.Musicista;
import com.grassi.partiturae.profilo.ProfiloUtenteService;
import com.grassi.partiturae.strumento.Strumento;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiePartiServiceTest {

    @Mock
    private ProfiloUtenteService profiloUtenteService;

    @Mock
    private ParteService parteService;

    @InjectMocks
    private MiePartiService service;

    @Test
    void senzaProfiloMusicaleNonCiSonoPartiEIlMotivoEChiaro() {
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.empty());

        MiePartiResponse risposta = service.mieParti(null);

        assertThat(risposta.isProfiloTrovato()).isFalse();
        assertThat(risposta.getParti()).isEmpty();
        verifyNoInteractions(parteService);
    }

    @Test
    void senzaStrumentiNonSiCercanoParti() {
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.of(musicista()));

        MiePartiResponse risposta = service.mieParti(null);

        assertThat(risposta.isProfiloTrovato()).isTrue();
        assertThat(risposta.getStrumenti()).isEmpty();
        assertThat(risposta.getParti()).isEmpty();
        verifyNoInteractions(parteService);
    }

    @Test
    void restituisceLePartiDegliStrumentiDelMusicista() {
        Strumento tromba = strumento(7L, "Tromba");
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.of(musicista(tromba)));
        when(parteService.getByStrumenti(List.of(7L))).thenReturn(List.of(parte(1L, 10L), parte(2L, 20L)));

        MiePartiResponse risposta = service.mieParti(null);

        assertThat(risposta.isProfiloTrovato()).isTrue();
        assertThat(risposta.getStrumenti()).containsExactly("Tromba");
        assertThat(risposta.getParti()).extracting(ParteResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void conUnaPartituraIndicataRestanoSoloLeSueParti() {
        Strumento tromba = strumento(7L, "Tromba");
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.of(musicista(tromba)));
        when(parteService.getByStrumenti(List.of(7L))).thenReturn(List.of(parte(1L, 10L), parte(2L, 20L)));

        MiePartiResponse risposta = service.mieParti(20L);

        assertThat(risposta.getParti()).extracting(ParteResponse::getId).containsExactly(2L);
    }

    @Test
    void ilPdfDiUnaPropriaParteSiScarica() {
        Strumento tromba = strumento(7L, "Tromba");
        StrumentoFiglio tromba2 = voce(21L, "Tromba 2", tromba);
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.of(musicista(tromba)));
        when(parteService.findEntityById(5L)).thenReturn(parteConVoci(5L, tromba2));
        when(parteService.getPdf(5L)).thenReturn(new FileDownloadResponse("Tromba2_Inno.pdf", new byte[] {1}));

        FileDownloadResponse file = service.pdf(5L);

        assertThat(file.filename()).isEqualTo("Tromba2_Inno.pdf");
    }

    @Test
    void ilPdfDiUnaParteDiUnAltroStrumentoNonSiScarica() {
        Strumento tromba = strumento(7L, "Tromba");
        StrumentoFiglio clarinetto1 = voce(31L, "Clarinetto 1", strumento(9L, "Clarinetto"));
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.of(musicista(tromba)));
        when(parteService.findEntityById(5L)).thenReturn(parteConVoci(5L, clarinetto1));

        assertThatThrownBy(() -> service.pdf(5L)).isInstanceOf(ResourceNotFoundException.class);
        verify(parteService, never()).getPdf(any());
    }

    @Test
    void senzaProfiloMusicaleNessunPdfSiScarica() {
        StrumentoFiglio tromba2 = voce(21L, "Tromba 2", strumento(7L, "Tromba"));
        when(profiloUtenteService.musicistaCorrente()).thenReturn(Optional.empty());
        when(parteService.findEntityById(5L)).thenReturn(parteConVoci(5L, tromba2));

        assertThatThrownBy(() -> service.pdf(5L)).isInstanceOf(ResourceNotFoundException.class);
        verify(parteService, never()).getPdf(any());
    }

    private static Strumento strumento(Long id, String nome) {
        return Strumento.builder().id(id).nome(nome).build();
    }

    private static StrumentoFiglio voce(Long id, String nome, Strumento strumento) {
        return StrumentoFiglio.builder().id(id).nome(nome).strumento(strumento).build();
    }

    private static Musicista musicista(Strumento... strumenti) {
        return Musicista.builder().strumenti(new HashSet<>(Set.of(strumenti))).build();
    }

    private static Parte parteConVoci(Long id, StrumentoFiglio... voci) {
        return Parte.builder().id(id).strumenti(new HashSet<>(Set.of(voci))).build();
    }

    private static ParteResponse parte(Long id, Long partituraId) {
        return ParteResponse.builder().id(id).partituraId(partituraId).partituraNome("Partitura " + partituraId).build();
    }
}