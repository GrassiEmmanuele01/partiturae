package com.grassi.partiturae.parte;

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
public class ParteStrumentiRequest {

    @NotEmpty(message = "Seleziona almeno uno strumento")
    private List<Long> strumentoFiglioIds;
}
