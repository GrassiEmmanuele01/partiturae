package com.grassi.partiturae.formazione;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Entity
@Table(name = "formazione")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    private Integer annoFondazione;

    private String indirizzo;

    private String codiceFiscale;

    private String email;

    private String telefono;

    private String sitoWeb;

    private String logoNome;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logo;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}