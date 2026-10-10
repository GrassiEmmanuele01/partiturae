package com.grassi.partiturae.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * Impostazioni di sicurezza lette da application.properties (prefisso "app.security").
 */
@ConfigurationProperties(prefix = "app.security")
public record AuthProperties(
        /** Chiave per firmare i token (almeno 32 caratteri). Se vuota se ne genera una a ogni avvio. */
        String jwtSecret,
        @DefaultValue("15") long accessTokenMinutes,
        @DefaultValue("7") long refreshTokenDays,
        /** True in produzione (HTTPS): il cookie di sessione viaggia solo su connessioni cifrate. */
        @DefaultValue("false") boolean cookieSecure,
        @DefaultValue("http://localhost:4200") List<String> allowedOrigins,
        /** Nome della prima banda, creata al primo avvio. */
        @DefaultValue("La mia banda") String initialBandName,
        @DefaultValue("admin@partiturae.local") String adminEmail,
        /** Password del primo amministratore. Se vuota viene generata e mostrata una sola volta nel log. */
        String adminPassword,
        @DefaultValue("5") int maxFailedAttempts,
        @DefaultValue("15") long lockMinutes) {
}