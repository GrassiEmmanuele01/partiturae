package com.grassi.partiturae.dto;

import jakarta.validation.constraints.NotBlank;
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
public class BandaRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 150)
    private String nome;

    private String descrizione;
}