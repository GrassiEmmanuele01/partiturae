package com.grassi.partiturae.dto;

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
public class TesseramentoRequest {

    @NotNull(message = "Lo stato di tesseramento è obbligatorio")
    private Boolean tesserato;
}