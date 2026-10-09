package com.grassi.partiturae.evento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponse {
    private Long id;
    private String titolo;
    private TipoEvento tipo;
    private LocalDate data;
    private LocalTime ora;
    private String luogo;
    private String note;
    private int numeroPresenti;
}