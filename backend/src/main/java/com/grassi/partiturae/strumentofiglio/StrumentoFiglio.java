package com.grassi.partiturae.strumentofiglio;

import com.grassi.partiturae.strumento.Strumento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "strumento_figlio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrumentoFiglio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strumento_id")
    private Strumento strumento;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}