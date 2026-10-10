package com.grassi.partiturae.auth;

import com.grassi.partiturae.banda.Banda;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Un account dentro una banda, con i ruoli che ha in quella banda.
 * La stessa persona può essere ADMIN in una banda e solo MUSICISTA in un'altra.
 */
@Entity
@Table(name = "appartenenza", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "banda_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appartenenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "banda_id")
    private Banda banda;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "appartenenza_ruolo", joinColumns = @JoinColumn(name = "appartenenza_id"))
    @Column(name = "ruolo", nullable = false)
    private Set<Ruolo> ruoli = new HashSet<>();

    /**
     * Il socio di questa banda collegato all'account. Si salva solo l'id (non il collegamento completo):
     * i soci appartengono a una banda e si leggono solo quando si lavora in quella banda.
     */
    private Long socioId;

    /** False = l'utente non fa più parte di questa banda. */
    @Builder.Default
    private boolean attiva = true;
}
