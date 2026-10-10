package com.grassi.partiturae.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Chi sta facendo la richiesta: l'account e la banda in cui sta lavorando, letti dal token.
 */
public final class UtenteCorrente {

    private UtenteCorrente() {
    }

    public static Optional<Long> accountId() {
        return token().map(t -> Long.valueOf(t.getToken().getSubject()));
    }

    public static Optional<Long> bandaId() {
        Optional<JwtAuthenticationToken> token = token();
        if (token.isEmpty()) {
            return Optional.empty();
        }

        Object banda = token.get().getToken().getClaim("banda");
        return banda instanceof Number numero ? Optional.of(numero.longValue()) : Optional.empty();
    }

    private static Optional<JwtAuthenticationToken> token() {
        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        return autenticazione instanceof JwtAuthenticationToken token ? Optional.of(token) : Optional.empty();
    }
}