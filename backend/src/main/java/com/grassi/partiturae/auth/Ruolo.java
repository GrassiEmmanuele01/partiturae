package com.grassi.partiturae.auth;

/**
 * Ruoli dell'applicazione. Un account può averne più di uno
 * (es. un archivista che suona anche nella formazione è ARCHIVISTA + MUSICISTA).
 */
public enum Ruolo {
    /** Gestisce tutto, compresi gli account degli altri. */
    ADMIN_BANDA,
    /** Direzione musicale: repertorio, raccolte, eventi. */
    MAESTRO,
    /** Cura l'archivio: partiture, parti e PDF. */
    ARCHIVISTA,
    /** Utente standard: vede solo le parti dei propri strumenti. */
    MUSICISTA
}