package com.grassi.partiturae.dto;

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
public class IscrizioneSocioSummaryResponse {
    private boolean iscrittoAnnoCorrente;
    private int anniIscritto;
    private List<Integer> anni;
}