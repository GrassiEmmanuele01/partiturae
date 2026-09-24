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
public class ParteResponse {
    private Long id;
    private String nome;
    private Boolean libretto;
    private String pdfNome;
    private Long partituraId;
    private String partituraNome;
    private Long strumentoFiglioId;
    private String strumentoFiglioNome;
}