package com.grassi.partiturae.banda;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Dice a Hibernate in quale banda si sta lavorando. Hibernate aggiunge da solo "banda_id = ..." a ogni
 * ricerca delle entità marcate con @TenantId e scrive la banda in ogni nuovo inserimento: così i dati
 * di una banda non possono finire nelle risposte di un'altra.
 *
 * La banda viene dal token firmato dell'utente (campo "banda"). Senza token, o con un token senza banda
 * (es. il superadmin), si lavora nella banda 0, che non esiste e quindi non contiene dati.
 *
 * Viene creata da Hibernate (vedi spring.jpa.properties.hibernate.tenant_identifier_resolver),
 * per questo ha un costruttore senza parametri e non usa bean di Spring.
 */
public class BandaTenantResolver implements CurrentTenantIdentifierResolver<Long> {

    public static final Long NESSUNA_BANDA = 0L;

    @Override
    public Long resolveCurrentTenantIdentifier() {
        Long forzata = BandaContext.forzata();
        if (forzata != null) {
            return forzata;
        }

        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        if (autenticazione instanceof JwtAuthenticationToken token) {
            Object banda = token.getToken().getClaim("banda");
            if (banda instanceof Number numero) {
                return numero.longValue();
            }
        }

        return NESSUNA_BANDA;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
