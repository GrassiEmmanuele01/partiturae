package com.grassi.partiturae.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PartituraRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 200)
    private String nome;

    private String descrizione;

    @Min(value = 1000, message = "Anno non valido")
    @Max(value = 2100, message = "Anno non valido")
    private Integer anno;

    @NotNull(message = "L'autore è obbligatorio")
    private Long autoreId;
}