package com.grassi.partiturae.auth;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Crea gli access token: brevi (15 minuti di default), firmati con HMAC-SHA256,
 * con dentro account, banda in cui si lavora e ruoli in quella banda.
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

    /**
     * @param bandaId la banda in cui si lavora (null per un superadmin che non è in nessuna banda)
     * @param ruoli   i ruoli dell'account in quella banda
     */
    public String creaAccessToken(Account account, Long bandaId, Set<Ruolo> ruoli) {
        Instant ora = Instant.now();

        List<String> ruoliNelToken = new ArrayList<>(ruoli.stream().map(Ruolo::name).sorted().toList());
        if (account.isSuperadmin()) {
            ruoliNelToken.add("SUPERADMIN");
        }

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(ora)
                .expiresAt(ora.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES))
                .subject(String.valueOf(account.getId()))
                .claim("email", account.getEmail())
                .claim("roles", ruoliNelToken);

        if (bandaId != null) {
            claims.claim("banda", bandaId);
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
