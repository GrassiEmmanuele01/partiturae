package com.grassi.partiturae.partitura;

import com.grassi.partiturae.autore.AutoreResponse;
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
    private TipoPartitura tipo;
    private AutoreResponse autore;
}