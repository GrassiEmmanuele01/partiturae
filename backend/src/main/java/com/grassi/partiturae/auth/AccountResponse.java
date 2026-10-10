package com.grassi.partiturae.auth;

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
public class AccountResponse {
    private Long id;
    private String email;
    private List<Ruolo> ruoli;
    private Long socioId;
    private String nome;
    private String cognome;
    private boolean deveCambiarePassword;
}