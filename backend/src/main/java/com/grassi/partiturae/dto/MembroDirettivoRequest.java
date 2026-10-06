package com.grassi.partiturae.dto;

import com.grassi.partiturae.model.CaricaDirettivo;

import jakarta.validation.constraints.NotNull;
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
public class MembroDirettivoRequest {

    @NotNull(message = "Il socio è obbligatorio")
    private Long socioId;

    @NotNull(message = "La carica è obbligatoria")
    private CaricaDirettivo carica;

    @NotNull(message = "L'anno di inizio è obbligatorio")
    private Integer annoInizio;

    private Integer annoFine;
}