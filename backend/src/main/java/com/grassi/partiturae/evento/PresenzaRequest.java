package com.grassi.partiturae.evento;

import jakarta.validation.constraints.NotNull;
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
public class PresenzaRequest {

    @NotNull(message = "Lo stato di presenza è obbligatorio")
    private Boolean presente;
}