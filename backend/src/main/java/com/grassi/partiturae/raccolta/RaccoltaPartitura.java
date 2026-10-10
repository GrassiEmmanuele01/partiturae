package com.grassi.partiturae.raccolta;

import com.grassi.partiturae.partitura.Partitura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Entity
@Table(name = "raccolta_partitura", uniqueConstraints = @UniqueConstraint(columnNames = {"raccolta_id", "partitura_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaccoltaPartitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raccolta_id")
    private Raccolta raccolta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partitura_id")
    private Partitura partitura;

    private Integer ordine;

    // Banda proprietaria del dato: la imposta Hibernate da solo e ogni ricerca vede solo la banda corrente.
    @TenantId
    @Column(name = "banda_id", nullable = false, updatable = false)
    private Long bandaId;
}