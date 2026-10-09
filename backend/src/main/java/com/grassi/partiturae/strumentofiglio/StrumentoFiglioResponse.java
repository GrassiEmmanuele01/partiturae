package com.grassi.partiturae.strumentofiglio;

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
public class StrumentoFiglioResponse {
    private Long id;
    private String nome;
    private Long strumentoId;
    private String strumentoNome;
}