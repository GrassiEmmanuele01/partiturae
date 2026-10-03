package com.grassi.partiturae.dto;

import java.util.List;

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
public class TesseramentoSummaryResponse {
    private boolean tesseratoAnnoCorrente;
    private int anniTesserato;
    private List<Integer> anni;
}