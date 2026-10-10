package com.grassi.partiturae.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * La persona che accede. Non appartiene a una banda: lo fa tramite le {@link Appartenenza}
 * (una per ogni banda, con i ruoli che ha lì).
 */
@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String nome;

    private String cognome;

    /** Gestisce la piattaforma (bande e account amministratore) ma non vede i dati delle bande. */
    @Builder.Default
    private boolean superadmin = false;

    /** False = account bloccato: non può entrare in nessuna banda. */
    @Builder.Default
    private boolean attivo = true;

    /** True per le password provvisorie assegnate da un amministratore. */
    @Builder.Default
    private boolean deveCambiarePassword = false;

    private int tentativiFalliti;

    private Instant bloccatoFino;

    private Instant ultimoAccesso;

    /** Ultima banda in cui si è lavorato: al prossimo accesso si riparte da lì. */
    private Long ultimaBandaId;
}
