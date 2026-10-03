package com.grassi.partiturae.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
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
public class ReorderPartitureRequest {

    @NotEmpty(message = "L'elenco non può essere vuoto")
    private List<Long> partituraIdsInOrdine;
}