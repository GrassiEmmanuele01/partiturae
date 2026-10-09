package com.grassi.partiturae.formazione;

import com.grassi.partiturae.direttivo.MembroDirettivoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormazioneResponse {
    private Long id;
    private String nome;
    private String descrizione;
    private Integer annoFondazione;
    private String indirizzo;
    private String codiceFiscale;
    private String email;
    private String telefono;
    private String sitoWeb;
    private int numeroAssociatiAnnoCorrente;
    private List<MembroDirettivoResponse> direttivoInCarica;
}