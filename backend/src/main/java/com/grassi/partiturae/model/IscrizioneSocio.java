package com.grassi.partiturae.model;

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

@Entity
@Table(name = "iscrizione_socio", uniqueConstraints = @UniqueConstraint(columnNames = {"socio_id", "anno"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IscrizioneSocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id")
    private Socio socio;

    private Integer anno;

    private Boolean iscritto;
}