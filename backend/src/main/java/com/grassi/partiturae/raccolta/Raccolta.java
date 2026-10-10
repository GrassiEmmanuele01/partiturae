package com.grassi.partiturae.raccolta;

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
import org.hibernate.annotations.TenantId;

@Entity
@Table(name = "raccolta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Raccolta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer anno;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}