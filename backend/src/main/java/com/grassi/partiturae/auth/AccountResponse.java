package com.grassi.partiturae.auth;

import com.grassi.partiturae.banda.BandaResponse;
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
    private String nome;
    private String cognome;
    private boolean superadmin;
    private boolean deveCambiarePassword;
    /** La banda in cui si sta lavorando (vuota per un superadmin che non è in nessuna banda). */
    private BandaResponse bandaCorrente;
    /** I ruoli che l'utente ha nella banda corrente. */
    private List<Ruolo> ruoli;
    /** Tutte le bande in cui l'utente può entrare (per cambiare banda). */
    private List<BandaResponse> bande;
    private Long socioId;
}
