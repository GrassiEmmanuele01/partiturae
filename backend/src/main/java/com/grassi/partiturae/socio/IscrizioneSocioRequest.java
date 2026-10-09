package com.grassi.partiturae.socio;

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
public class IscrizioneSocioRequest {

    @NotNull(message = "Lo stato di iscrizione è obbligatorio")
    private Boolean iscritto;
}