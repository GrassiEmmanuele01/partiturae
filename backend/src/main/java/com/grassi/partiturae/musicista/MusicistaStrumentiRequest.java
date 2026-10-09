package com.grassi.partiturae.musicista;

import jakarta.validation.constraints.NotNull;
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
public class MusicistaStrumentiRequest {

    @NotNull(message = "L'elenco strumenti è obbligatorio (può essere vuoto)")
    private List<Long> strumentoIds;
}