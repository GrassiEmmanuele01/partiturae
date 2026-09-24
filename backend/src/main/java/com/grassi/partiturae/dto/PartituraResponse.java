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
public class PartituraResponse {
    private Long id;
    private String nome;
    private String descrizione;
    private Integer anno;
    private AutoreResponse autore;
}