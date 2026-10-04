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
public class SocioResponse {
    private Long id;
    private String nome;
    private String cognome;
    private String mail;
    private String codiceFiscale;
    private String telefono;
    private boolean aggiunto;
}