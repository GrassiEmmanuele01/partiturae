package com.grassi.partiturae.banda;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Una banda (o formazione) che usa l'applicazione. Tutti i dati di lavoro (soci, partiture, eventi...)
 * appartengono a una banda e le altre non li vedono mai.
 */
@Entity
@Table(name = "banda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    /** False = banda bloccata dal superadmin: nessuno dei suoi utenti può entrare. */
    @Builder.Default
    private boolean attiva = true;

    @Column(nullable = false, updatable = false)
    private Instant creataIl;

    @PrePersist
    void impostaDataCreazione() {
        if (creataIl == null) {
            creataIl = Instant.now();
        }
    }
}
