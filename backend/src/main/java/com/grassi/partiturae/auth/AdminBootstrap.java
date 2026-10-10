package com.grassi.partiturae.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Al primo avvio, se non esiste nessun account, crea il primo amministratore.
 * La password non sta nel codice: o la si passa con PARTITURAE_ADMIN_PASSWORD
 * oppure viene generata e scritta una sola volta nel log.
 */
@Slf4j
@Component
public class AdminBootstrap implements CommandLineRunner {

    private static final String ALFABETO = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties properties;

    public AdminBootstrap(AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           AuthProperties properties) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;
        }

        boolean generata = properties.adminPassword() == null || properties.adminPassword().isBlank();
        String password = generata ? passwordCasuale(16) : properties.adminPassword();

        Set<Ruolo> ruoli = new HashSet<>();
        ruoli.add(Ruolo.ADMIN_BANDA);

        accountRepository.save(Account.builder()
                .email(properties.adminEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(password))
                .ruoli(ruoli)
                .attivo(true)
                .deveCambiarePassword(true)
                .build());

        if (generata) {
            log.warn("""

                    ==================================================================
                      Creato il primo account amministratore
                        email:    {}
                        password: {}
                      Questa password non verrà mostrata più: annotala e cambiala.
                    ==================================================================
                    """, properties.adminEmail(), password);
        } else {
            log.info("Creato il primo account amministratore: {}", properties.adminEmail());
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