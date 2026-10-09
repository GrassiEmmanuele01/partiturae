package com.grassi.partiturae.raccolta;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderPartitureRequest {

    @NotEmpty(message = "L'elenco non può essere vuoto")
    private List<Long> partituraIdsInOrdine;
}