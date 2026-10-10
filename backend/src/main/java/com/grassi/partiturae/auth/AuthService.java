package com.grassi.partiturae.auth;

import com.grassi.partiturae.common.exception.AuthException;
import com.grassi.partiturae.socio.Socio;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {

    private static final String CREDENZIALI_NON_VALIDE = "Email o password non corrette.";
    private static final String SESSIONE_SCADUTA = "Sessione scaduta: effettua di nuovo l'accesso.";
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Risultato di login/rinnovo: la risposta per il client e il refresh token da mettere nel cookie. */
    public record AuthResult(AuthResponse risposta, String refreshToken) {
    }

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthProperties properties;
    private final String hashFittizio;

    public AuthService(AccountRepository accountRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuthProperties properties) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.properties = properties;
        // Serve a far durare uguale la risposta anche quando l'email non esiste (vedi login).
        this.hashFittizio = passwordEncoder.encode("password-fittizia-per-tempi-costanti");
    }

    // noRollbackFor: i tentativi falliti devono restare salvati anche se lanciamo l'errore.
    @Transactional(noRollbackFor = AuthException.class)
    public AuthResult login(String email, String password) {
        Account account = accountRepository.findByEmailIgnoreCase(email.trim()).orElse(null);

        if (account == null) {
            // Si controlla comunque una password finta: così chi prova email a caso
            // non capisce, dai tempi di risposta, quali esistono.
            passwordEncoder.matches(password, hashFittizio);
            throw new AuthException(HttpStatus.UNAUTHORIZED, CREDENZIALI_NON_VALIDE);
        }

        Instant ora = Instant.now();

        if (account.getBloccatoFino() != null && account.getBloccatoFino().isAfter(ora)) {
            throw new AuthException(HttpStatus.TOO_MANY_REQUESTS,
                    "Troppi tentativi falliti. Riprova tra qualche minuto.");
        }

        if (!account.isAttivo() || !passwordEncoder.matches(password, account.getPasswordHash())) {
            registraFallimento(account, ora);
            throw new AuthException(HttpStatus.UNAUTHORIZED, CREDENZIALI_NON_VALIDE);
        }

        account.setTentativiFalliti(0);
        account.setBloccatoFino(null);
        account.setUltimoAccesso(ora);
        accountRepository.save(account);

        return emettiToken(account, ora);
    }

    @Transactional(noRollbackFor = AuthException.class)
    public AuthResult refresh(String refreshTokenGrezzo) {
        if (refreshTokenGrezzo == null || refreshTokenGrezzo.isBlank()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA);
        }

        RefreshToken salvato = refreshTokenRepository.findByTokenHash(hash(refreshTokenGrezzo))
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA));

        Instant ora = Instant.now();

        if (salvato.getRevocatoIl() != null) {
            // Un token già usato che ricompare: potrebbe essere stato rubato.
            // Per prudenza si chiudono tutte le sessioni dell'account.
            refreshTokenRepository.revocaTutti(salvato.getAccount().getId(), ora);
            throw new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA);
        }

        if (salvato.getScadenza().isBefore(ora) || !salvato.getAccount().isAttivo()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA);
        }

        // Rotazione: ogni token si può usare una volta sola, poi se ne emette uno nuovo.
        salvato.setRevocatoIl(ora);
        refreshTokenRepository.save(salvato);

        return emettiToken(salvato.getAccount(), ora);
    }

    @Transactional
    public void logout(String refreshTokenGrezzo) {
        if (refreshTokenGrezzo == null || refreshTokenGrezzo.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(hash(refreshTokenGrezzo)).ifPresent(token -> {
            if (token.getRevocatoIl() == null) {
                token.setRevocatoIl(Instant.now());
                refreshTokenRepository.save(token);
            }
        });
    }

    @Transactional(readOnly = true)
    public AccountResponse me(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA));

        return toResponse(account);
    }

    private void registraFallimento(Account account, Instant ora) {
        int tentativi = account.getTentativiFalliti() + 1;

        if (tentativi >= properties.maxFailedAttempts()) {
            account.setBloccatoFino(ora.plus(properties.lockMinutes(), ChronoUnit.MINUTES));
            tentativi = 0;
        }

        account.setTentativiFalliti(tentativi);
        accountRepository.save(account);
    }

    private AuthResult emettiToken(Account account, Instant ora) {
        String refreshGrezzo = generaTokenCasuale();

        refreshTokenRepository.save(RefreshToken.builder()
                .account(account)
                .tokenHash(hash(refreshGrezzo))
                .creatoIl(ora)
                .scadenza(ora.plus(properties.refreshTokenDays(), ChronoUnit.DAYS))
                .build());

        AuthResponse risposta = AuthResponse.builder()
                .accessToken(jwtService.creaAccessToken(account))
                .tokenType("Bearer")
                .expiresIn(properties.accessTokenMinutes() * 60)
                .account(toResponse(account))
                .build();

        return new AuthResult(risposta, refreshGrezzo);
    }

    private AccountResponse toResponse(Account account) {
        Socio socio = account.getSocio();

        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .ruoli(account.getRuoli().stream().sorted().toList())
                .socioId(socio != null ? socio.getId() : null)
                .nome(socio != null ? socio.getNome() : null)
                .cognome(socio != null ? socio.getCognome() : null)
                .deveCambiarePassword(account.isDeveCambiarePassword())
                .build();
    }

    private static String generaTokenCasuale() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String valore) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(valore.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponibile", e);
        }
    }
}