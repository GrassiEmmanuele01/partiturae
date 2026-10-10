package com.grassi.partiturae.auth;

/**
 * Ruoli dentro una banda. Un account può averne più d'uno nella stessa banda (valgono tutti insieme)
 * e ruoli diversi in bande diverse. Il superadmin non è un ruolo di banda: è una proprietà dell'account.
 */
public enum Ruolo {
    /** L'IT della banda: tutte le funzioni, account e password degli utenti della banda. */
    ADMIN,
    /** Archivio: partiture, raccolte, autori, soci, musicisti, entrambi i calendari. */
    ARCHIVISTA,
    /** Maestro della banda: aggiunge e modifica partiture, le mette in raccolte, calendario banda. */
    MAESTRO,
    /** Maestro della sezione giovanile: come il maestro ma per la sezione allievi. */
    MAESTROALLIEVI,
    /** Direttivo: soci, musicisti e allievi, informazioni della banda, entrambi i calendari. */
    DIRETTIVO,
    /** Suona in banda: vede le parti del suo strumento e il calendario banda. */
    MUSICISTA,
    /** Socio della sezione giovanile: vede le parti degli allievi e il calendario allievi. */
    ALLIEVO,
    /** Socio senza strumento: informazioni sulla banda e calendario. */
    SOCIO
}
