package com.grassi.partiturae.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembroDirettivoResponse {
    private Long id;
    private SocioResponse socio;
    private String carica;
    private Integer annoInizio;
    private Integer annoFine;
    private boolean inCarica;
}