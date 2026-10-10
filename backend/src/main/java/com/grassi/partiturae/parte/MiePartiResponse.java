package com.grassi.partiturae.parte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Le parti dei propri strumenti. Se la persona non ha un profilo musicale (o non è collegata a un socio)
 * {@code profiloTrovato} è false, così l'interfaccia può spiegare perché l'elenco è vuoto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiePartiResponse {
    private boolean profiloTrovato;
    /** I nomi degli strumenti del profilo musicale (es. Tromba, Flicorno). */
    private List<String> strumenti;
    private List<ParteResponse> parti;
}