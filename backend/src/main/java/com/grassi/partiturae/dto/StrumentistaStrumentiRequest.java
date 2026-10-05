package com.grassi.partiturae.dto;

import java.util.List;

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
public class StrumentistaStrumentiRequest {

    @NotNull(message = "L'elenco strumenti è obbligatorio (può essere vuoto)")
    private List<Long> strumentoIds;
}