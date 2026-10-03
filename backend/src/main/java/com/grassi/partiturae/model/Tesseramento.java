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
@Table(name = "tesseramento", uniqueConstraints = @UniqueConstraint(columnNames = {"bandista_id", "anno"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tesseramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bandista_id")
    private Bandista bandista;

    private Integer anno;

    private Boolean tesserato;
}