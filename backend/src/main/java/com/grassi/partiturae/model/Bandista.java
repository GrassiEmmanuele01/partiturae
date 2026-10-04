package com.grassi.partiturae.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bandista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bandista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", unique = true)
    private Socio socio;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bandista_strumento",
        joinColumns = @JoinColumn(name = "bandista_id"),
        inverseJoinColumns = @JoinColumn(name = "strumento_id")
    )
    private Set<Strumento> strumenti = new HashSet<>();
}