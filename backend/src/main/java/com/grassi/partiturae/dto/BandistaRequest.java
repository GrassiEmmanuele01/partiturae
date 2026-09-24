package com.grassi.partiturae.dto;

import jakarta.validation.constraints.Email;
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
public class BandistaRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 100)
    private String cognome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    @Size(max = 150)
    private String mail;

    @Size(min = 16, max = 16, message = "Il codice fiscale deve avere 16 caratteri")
    private String codiceFiscale;

    @Size(max = 50)
    private String telefono;
}