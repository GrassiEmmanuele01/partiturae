package com.grassi.partiturae.dto;

import com.grassi.partiturae.model.CaricaDirettivo;

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
    private CaricaDirettivo carica;
    private Integer annoInizio;
    private Integer annoFine;
    private boolean inCarica;
}