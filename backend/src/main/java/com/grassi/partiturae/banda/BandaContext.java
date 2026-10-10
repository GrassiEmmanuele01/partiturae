package com.grassi.partiturae.banda;

/**
 * Permette al codice interno (avvio dell'applicazione, creazione di una banda) di lavorare "dentro" una banda
 * senza che ci sia un utente collegato. Le richieste normali non lo usano: la banda arriva dal token.
 */
public final class BandaContext {

    private static final ThreadLocal<Long> FORZATA = new ThreadLocal<>();

    private BandaContext() {
    }

    /**
     * Esegue l'azione nella banda indicata. Le transazioni vanno aperte DENTRO l'azione:
     * la banda di una sessione di database si decide quando la sessione viene aperta.
     */
    public static void esegui(Long bandaId, Runnable azione) {
        Long precedente = FORZATA.get();
        FORZATA.set(bandaId);
        try {
            azione.run();
        } finally {
            if (precedente == null) {
                FORZATA.remove();
            } else {
                FORZATA.set(precedente);
            }
        }
    }

    static Long forzata() {
        return FORZATA.get();
    }
}
