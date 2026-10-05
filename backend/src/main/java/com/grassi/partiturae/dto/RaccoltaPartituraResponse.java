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
public class RaccoltaPartituraResponse {
    private Integer ordine;
    private PartituraResponse partitura;
}