package com.grassi.partiturae.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class FamigliaRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100)
    private String nome;
}