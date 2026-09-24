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
public class AutoreRequest {

    @NotBlank(message = "Il nominativo è obbligatorio")
    @Size(max = 200)
    private String nominativo;
}