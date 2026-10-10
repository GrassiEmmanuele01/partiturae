package com.grassi.partiturae.socio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

// Codice fiscale ed email sono unici dentro una banda: la stessa persona può essere socia di due bande.
@Entity
@Table(name = "socio", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"banda_id", "codice_fiscale"}),
        @UniqueConstraint(columnNames = {"banda_id", "mail"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16)
    private String codiceFiscale;

    private String nome;

    private String cognome;

    private String mail;

    private String telefono;

    @Builder.Default
    private Boolean aggiunto = false;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}
