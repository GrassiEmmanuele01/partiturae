package com.grassi.partiturae.formazione;

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
public class FormazioneRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 150)
    private String nome;

    private String descrizione;

    private Integer annoFondazione;

    private String indirizzo;

    private String codiceFiscale;

    private String email;

    private String telefono;

    private String sitoWeb;
}