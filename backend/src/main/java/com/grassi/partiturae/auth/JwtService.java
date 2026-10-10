package com.grassi.partiturae.auth;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Crea gli access token: brevi (15 minuti di default), firmati con HMAC-SHA256,
 * con dentro id, email e ruoli dell'account.
 */
@Service
public class JwtService {

    public static final String ISSUER = "partiturae";

    private final JwtEncoder jwtEncoder;
    private final AuthProperties properties;

    public JwtService(JwtEncoder jwtEncoder, AuthProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public String creaAccessToken(Account account) {
        Instant ora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(ora)
                .expiresAt(ora.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES))
                .subject(String.valueOf(account.getId()))
                .claim("email", account.getEmail())
                .claim("roles", account.getRuoli().stream().map(Ruolo::name).sorted().toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}