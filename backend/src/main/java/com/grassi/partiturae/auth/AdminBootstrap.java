package com.grassi.partiturae.auth;

import com.grassi.partiturae.banda.Banda;
import com.grassi.partiturae.banda.BandaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Al primo avvio crea la prima banda e, se non esiste nessun account, il primo account: superadmin della
 * piattaforma e ADMIN di quella banda. La password non sta nel codice: o la si passa con
 * PARTITURAE_ADMIN_PASSWORD oppure viene generata e scritta una sola volta nel log.
 */
@Slf4j
@Component
@Order(1)
public class AdminBootstrap implements CommandLineRunner {

    private static final String ALFABETO = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final BandaRepository bandaRepository;
    private final AccountRepository accountRepository;
    private final AppartenenzaRepository appartenenzaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties properties;

    public AdminBootstrap(BandaRepository bandaRepository,
                           AccountRepository accountRepository,
                           AppartenenzaRepository appartenenzaRepository,
                           PasswordEncoder passwordEncoder,
                           AuthProperties properties) {
        this.bandaRepository = bandaRepository;
        this.accountRepository = accountRepository;
        this.appartenenzaRepository = appartenenzaRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Banda banda = bandaRepository.findAll().stream().findFirst()
                .orElseGet(() -> bandaRepository.save(Banda.builder()
                        .nome(properties.initialBandName().trim())
                        .build()));

        if (accountRepository.count() > 0) {
            return;
        }

        boolean generata = properties.adminPassword() == null || properties.adminPassword().isBlank();
        String password = generata ? passwordCasuale(16) : properties.adminPassword();

        Account account = accountRepository.save(Account.builder()
                .email(properties.adminEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(password))
                .superadmin(true)
                .attivo(true)
                .deveCambiarePassword(true)
                .ultimaBandaId(banda.getId())
                .build());

        Set<Ruolo> ruoli = new HashSet<>();
        ruoli.add(Ruolo.ADMIN);

        appartenenzaRepository.save(Appartenenza.builder()
                .account(account)
                .banda(banda)
                .ruoli(ruoli)
                .attiva(true)
                .build());

        if (generata) {
            log.warn("""

                    ==================================================================
                      Creato il primo account (superadmin e ADMIN della banda "{}")
                        email:    {}
                        password: {}
                      Questa password non verrà mostrata più: annotala e cambiala.
                    ==================================================================
                    """, banda.getNome(), properties.adminEmail(), password);
        } else {
            log.info("Creato il primo account: {} (banda \"{}\")", properties.adminEmail(), banda.getNome());
        }
    }

    private static String passwordCasuale(int lunghezza) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(lunghezza);
        for (int i = 0; i < lunghezza; i++) {
            sb.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }
}
