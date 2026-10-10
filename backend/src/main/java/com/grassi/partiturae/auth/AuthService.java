package com.grassi.partiturae.auth;

import com.grassi.partiturae.banda.BandaResponse;
import com.grassi.partiturae.common.exception.AuthException;
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
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private static final String CREDENZIALI_NON_VALIDE = "Email o password non corrette.";
    private static final String SESSIONE_SCADUTA = "Sessione scaduta: effettua di nuovo l'accesso.";
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Risultato di login/rinnovo: la risposta per il client e il refresh token da mettere nel cookie. */
    public record AuthResult(AuthResponse risposta, String refreshToken) {
    }

    private final AccountRepository accountRepository;
    private final AppartenenzaRepository appartenenzaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthProperties properties;
    private final String hashFittizio;

    public AuthService(AccountRepository accountRepository,
                        AppartenenzaRepository appartenenzaRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuthProperties properties) {
        this.accountRepository = accountRepository;
        this.appartenenzaRepository = appartenenzaRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.properties = properties;
        // Serve a far durare uguale la risposta anche quando l'email non esiste (vedi login).
        this.hashFittizio = passwordEncoder.encode("password-fittizia-per-tempi-costanti");
    }

    // noRollbackFor: i tentativi falliti devono restare salvati anche se lanciamo l'errore.
    @Transactional(noRollbackFor = AuthException.class)
    public AuthResult login(String email, String password, Long bandaRichiesta) {
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

        return emettiToken(account, bandaRichiesta, ora);
    }

    @Transactional(noRollbackFor = AuthException.class)
    public AuthResult refresh(String refreshTokenGrezzo) {
        return rinnova(refreshTokenGrezzo, null, null);
    }

    /** Passa a un'altra banda in cui l'utente è presente: si emette una nuova coppia di token per quella banda. */
    @Transactional(noRollbackFor = AuthException.class)
    public AuthResult cambiaBanda(String refreshTokenGrezzo, Long bandaId, Long accountId) {
        return rinnova(refreshTokenGrezzo, bandaId, accountId);
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
    public AccountResponse me(Long accountId, Long bandaId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA));

        List<Appartenenza> attive = appartenenzaRepository.findAttiveByAccount(accountId);
        Appartenenza corrente = attive.stream()
                .filter(a -> a.getBanda().getId().equals(bandaId))
                .findFirst()
                .orElse(null);

        return toResponse(account, corrente, attive);
    }

    private AuthResult rinnova(String refreshTokenGrezzo, Long bandaScelta, Long accountAtteso) {
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

        Account account = salvato.getAccount();

        if (salvato.getScadenza().isBefore(ora) || !account.isAttivo()
                || (accountAtteso != null && !accountAtteso.equals(account.getId()))) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, SESSIONE_SCADUTA);
        }

        // Rotazione: ogni token si può usare una volta sola, poi se ne emette uno nuovo.
        salvato.setRevocatoIl(ora);
        refreshTokenRepository.save(salvato);

        // Si resta nella banda della sessione, salvo che l'utente ne abbia scelta un'altra.
        Long banda = bandaScelta != null ? bandaScelta : salvato.getBandaId();

        return emettiToken(account, banda, ora);
    }

    /**
     * Sceglie la banda in cui lavorare: quella richiesta (se l'utente ne fa parte), altrimenti l'ultima usata,
     * altrimenti la prima in ordine alfabetico. Restituisce null solo per un superadmin senza bande.
     */
    private Appartenenza scegliBanda(Account account, List<Appartenenza> attive, Long richiesta) {
        if (richiesta != null) {
            return attive.stream()
                    .filter(a -> a.getBanda().getId().equals(richiesta))
                    .findFirst()
                    .orElseThrow(() -> new AuthException(HttpStatus.FORBIDDEN, "Non fai parte di questa banda."));
        }

        if (attive.isEmpty()) {
            if (account.isSuperadmin()) {
                return null;
            }
            throw new AuthException(HttpStatus.FORBIDDEN, "Il tuo account non è collegato a nessuna banda attiva.");
        }

        Long ultima = account.getUltimaBandaId();
        return attive.stream()
                .filter(a -> a.getBanda().getId().equals(ultima))
                .findFirst()
                .orElse(attive.get(0));
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

    private AuthResult emettiToken(Account account, Long bandaRichiesta, Instant ora) {
        List<Appartenenza> attive = appartenenzaRepository.findAttiveByAccount(account.getId());
        Appartenenza corrente = scegliBanda(account, attive, bandaRichiesta);
        Long bandaId = corrente != null ? corrente.getBanda().getId() : null;

        if (bandaId != null) {
            account.setUltimaBandaId(bandaId);
        }
        accountRepository.save(account);

        String refreshGrezzo = generaTokenCasuale();

        refreshTokenRepository.save(RefreshToken.builder()
                .account(account)
                .tokenHash(hash(refreshGrezzo))
                .creatoIl(ora)
                .scadenza(ora.plus(properties.refreshTokenDays(), ChronoUnit.DAYS))
                .bandaId(bandaId)
                .build());

        Set<Ruolo> ruoli = corrente != null ? corrente.getRuoli() : Set.of();

        AuthResponse risposta = AuthResponse.builder()
                .accessToken(jwtService.creaAccessToken(account, bandaId, ruoli))
                .tokenType("Bearer")
                .expiresIn(properties.accessTokenMinutes() * 60)
                .account(toResponse(account, corrente, attive))
                .build();

        return new AuthResult(risposta, refreshGrezzo);
    }

    private AccountResponse toResponse(Account account, Appartenenza corrente, List<Appartenenza> attive) {
        List<BandaResponse> bande = attive.stream()
                .map(a -> BandaResponse.builder().id(a.getBanda().getId()).nome(a.getBanda().getNome()).build())
                .toList();

        BandaResponse bandaCorrente = corrente == null ? null : BandaResponse.builder()
                .id(corrente.getBanda().getId())
                .nome(corrente.getBanda().getNome())
                .build();

        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .nome(account.getNome())
                .cognome(account.getCognome())
                .superadmin(account.isSuperadmin())
                .deveCambiarePassword(account.isDeveCambiarePassword())
                .bandaCorrente(bandaCorrente)
                .ruoli(corrente == null ? List.of() : corrente.getRuoli().stream().sorted().toList())
                .bande(bande)
                .socioId(corrente == null ? null : corrente.getSocioId())
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
