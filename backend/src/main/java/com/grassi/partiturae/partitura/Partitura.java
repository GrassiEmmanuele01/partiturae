package com.grassi.partiturae.partitura;

import com.grassi.partiturae.autore.Autore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Entity
@Table(name = "partitura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    private Integer anno;

    @Enumerated(EnumType.STRING)
    private TipoPartitura tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autore_id")
    private Autore autore;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}