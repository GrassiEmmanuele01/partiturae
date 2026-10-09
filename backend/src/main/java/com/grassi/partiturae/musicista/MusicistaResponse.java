package com.grassi.partiturae.musicista;

import com.grassi.partiturae.socio.SocioResponse;
import com.grassi.partiturae.strumento.StrumentoResponse;
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
public class MusicistaResponse {
    private Long id;
    private SocioResponse socio;
    private List<StrumentoResponse> strumenti;
}