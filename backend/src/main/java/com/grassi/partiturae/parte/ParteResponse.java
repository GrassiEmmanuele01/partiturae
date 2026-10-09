package com.grassi.partiturae.parte;

import com.grassi.partiturae.strumentofiglio.StrumentoFiglioResponse;
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
public class ParteResponse {
    private Long id;
    private Long partituraId;
    private String partituraNome;
    private List<StrumentoFiglioResponse> strumenti;
    private boolean pdfPresente;
    private String pdfNome;
}
