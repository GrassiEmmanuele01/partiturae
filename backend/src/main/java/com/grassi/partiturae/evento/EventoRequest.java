package com.grassi.partiturae.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EventoRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(max = 150)
    private String titolo;

    @NotNull(message = "Il tipo è obbligatorio")
    private TipoEvento tipo;

    @NotNull(message = "La data è obbligatoria")
    private LocalDate data;

    private LocalTime ora;

    private String luogo;

    private String note;
}