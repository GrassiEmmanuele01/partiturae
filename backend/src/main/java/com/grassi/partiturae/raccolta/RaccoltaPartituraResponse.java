package com.grassi.partiturae.raccolta;

import com.grassi.partiturae.partitura.PartituraResponse;
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