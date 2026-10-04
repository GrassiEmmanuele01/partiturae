package com.grassi.partiturae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "La carica è obbligatoria")
    @Size(max = 100)
    private String carica;

    @NotNull(message = "L'anno di inizio è obbligatorio")
    private Integer annoInizio;

    private Integer annoFine;
}