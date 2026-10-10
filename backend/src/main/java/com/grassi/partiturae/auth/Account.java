package com.grassi.partiturae.auth;

import com.grassi.partiturae.socio.Socio;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Credenziali di accesso. Il collegamento al socio è facoltativo: il primo amministratore
 * esiste prima che ci sia un libro soci; gli account dei musicisti verranno collegati al loro socio.
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

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "account_ruolo", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "ruolo", nullable = false)
    private Set<Ruolo> ruoli = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", unique = true)
    private Socio socio;

    @Builder.Default
    private boolean attivo = true;

    /** True per le password provvisorie assegnate da un amministratore. */
    @Builder.Default
    private boolean deveCambiarePassword = false;

    private int tentativiFalliti;

    private Instant bloccatoFino;

    private Instant ultimoAccesso;
}