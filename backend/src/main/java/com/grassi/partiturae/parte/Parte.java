package com.grassi.partiturae.parte;

import com.grassi.partiturae.partitura.Partitura;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Una "parte" è un PDF di una partitura che vale per uno o più strumenti
 * (es. "Corno in Fa 1-2" è un solo PDF collegato a due strumenti).
 */
@Entity
@Table(name = "parte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partitura_id")
    private Partitura partitura;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "parte_strumento",
        joinColumns = @JoinColumn(name = "parte_id"),
        inverseJoinColumns = @JoinColumn(name = "strumento_figlio_id")
    )
    private Set<StrumentoFiglio> strumenti = new HashSet<>();

    // Il PDF sta in una tabella a parte e viene caricato solo quando serve (download/merge):
    // così elencare le parti non trascina in memoria tutti i file.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "documento_id")
    private ParteDocumento documento;
}
