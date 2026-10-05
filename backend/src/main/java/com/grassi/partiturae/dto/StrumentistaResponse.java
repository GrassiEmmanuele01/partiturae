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
public class StrumentistaResponse {
    private Long id;
    private SocioResponse socio;
    private List<StrumentoResponse> strumenti;
}