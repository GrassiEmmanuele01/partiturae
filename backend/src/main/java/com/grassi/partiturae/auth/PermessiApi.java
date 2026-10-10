package com.grassi.partiturae.auth;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Chi può fare cosa. Tutta la tabella dei permessi sta in questo file.
 *
 * Le regole si leggono dall'alto e vale la prima che corrisponde alla richiesta, quindi i casi particolari
 * stanno prima di quelli generici. Per ogni area le richieste sono divise così:
 *   GET                       = leggere
 *   POST / PUT                = aggiungere e modificare
 *   DELETE                    = eliminare
 *
 * Quello che non è elencato è vietato a tutti. Il superadmin non ha nessun ruolo di banda, quindi
 * non passa da nessuna di queste regole: non vede i dati delle bande.
 *
 * Le stesse regole sono nell'interfaccia (frontend/src/app/features/auth/permessi.ts), che le usa solo per
 * nascondere ciò che non si può usare: se ne cambi una, cambiala in entrambi i posti.
 */
public class PermessiApi
        implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    private static final String ADMIN = Ruolo.ADMIN.name();
    private static final String ARCHIVISTA = Ruolo.ARCHIVISTA.name();
    private static final String MAESTRO = Ruolo.MAESTRO.name();
    private static final String MAESTROALLIEVI = Ruolo.MAESTROALLIEVI.name();
    private static final String DIRETTIVO = Ruolo.DIRETTIVO.name();
    private static final String MUSICISTA = Ruolo.MUSICISTA.name();
    private static final String ALLIEVO = Ruolo.ALLIEVO.name();
    private static final String SOCIO = Ruolo.SOCIO.name();

    /** Chiunque sia dentro una banda. */
    private static final String[] TUTTI = {ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, DIRETTIVO, MUSICISTA, ALLIEVO, SOCIO};

    /** Chi vede i dati personali dei soci e gestisce il libro soci. */
    private static final String[] SEGRETERIA = {ADMIN, ARCHIVISTA, DIRETTIVO};

    /** Chi può modificare le informazioni e il direttivo della banda. */
    private static final String[] DIREZIONE_BANDA = {ADMIN, DIRETTIVO};

    /** Chi aggiunge e modifica partiture, parti, raccolte e strumenti (non elimina, se non è archivista o admin). */
    private static final String[] CHI_COMPONE = {ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI};

    /** Chi elimina dall'archivio. */
    private static final String[] GESTORI_ARCHIVIO = {ADMIN, ARCHIVISTA};

    /** Chi suona e vede le parti dei propri strumenti. Chi suona e ha anche altri compiti ha entrambi i ruoli. */
    private static final String[] MUSICISTI = {MUSICISTA, ALLIEVO};

    /** Chi consulta il repertorio (titoli, autori, raccolte). Il socio semplice no. */
    private static final String[] CHI_LEGGE_REPERTORIO = {ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, MUSICISTA, ALLIEVO};

    /** Chi legge il catalogo degli strumenti (serve anche al direttivo per assegnarli ai musicisti). */
    private static final String[] TUTTI_TRANNE_SOCIO = {ADMIN, ARCHIVISTA, MAESTRO, MAESTROALLIEVI, DIRETTIVO, MUSICISTA, ALLIEVO};

    /** Chi gestisce i calendari e le presenze. */
    private static final String[] CALENDARI = {ADMIN, ARCHIVISTA, DIRETTIVO, MAESTRO, MAESTROALLIEVI};

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                // --- accesso ---
                .requestMatchers("/error").permitAll()
                .requestMatchers(POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                .requestMatchers("/api/auth/**").authenticated()

                // --- comuni a tutta la banda ---
                .requestMatchers(GET, "/api/formazione/logo").hasAnyRole(TUTTI)
                .requestMatchers(GET, "/api/*/*/utilizzo").hasAnyRole(GESTORI_ARCHIVIO)

                // --- libro soci e musicisti ---
                .requestMatchers("/api/soci/**", "/api/musicisti/**").hasAnyRole(SEGRETERIA)

                // --- direttivo e informazioni della banda ---
                .requestMatchers(GET, "/api/direttivo/**", "/api/formazione/**").hasAnyRole(SEGRETERIA)
                .requestMatchers("/api/direttivo/**", "/api/formazione/**").hasAnyRole(DIREZIONE_BANDA)

                // --- catalogo strumenti e autori ---
                .requestMatchers(GET, "/api/famiglie/**", "/api/strumenti/**", "/api/strumenti-figli/**", "/api/autori/**")
                        .hasAnyRole(TUTTI_TRANNE_SOCIO)
                .requestMatchers(DELETE, "/api/famiglie/**", "/api/strumenti/**", "/api/strumenti-figli/**", "/api/autori/**")
                        .hasAnyRole(GESTORI_ARCHIVIO)
                .requestMatchers("/api/famiglie/**", "/api/strumenti/**", "/api/strumenti-figli/**", "/api/autori/**")
                        .hasAnyRole(CHI_COMPONE)

                // --- le mie parti: le parti dei propri strumenti (il collegamento è fra l'account e il suo socio) ---
                .requestMatchers(GET, "/api/mie-parti/**").hasAnyRole(MUSICISTI)

                // --- partiture ---
                .requestMatchers(GET, "/api/partiture/**").hasAnyRole(CHI_LEGGE_REPERTORIO)
                .requestMatchers(DELETE, "/api/partiture/**").hasAnyRole(GESTORI_ARCHIVIO)
                .requestMatchers("/api/partiture/**").hasAnyRole(CHI_COMPONE)

                // --- parti e PDF ---
                // (musicisti e allievi usano /api/mie-parti, che mostra solo le parti dei loro strumenti)
                .requestMatchers(DELETE, "/api/parti/**").hasAnyRole(GESTORI_ARCHIVIO)
                .requestMatchers("/api/parti/**").hasAnyRole(CHI_COMPONE)

                // --- raccolte ---
                .requestMatchers(DELETE, "/api/raccolte/*/partiture/*").hasAnyRole(CHI_COMPONE)
                .requestMatchers(GET, "/api/raccolte/**").hasAnyRole(CHI_LEGGE_REPERTORIO)
                .requestMatchers(DELETE, "/api/raccolte/**").hasAnyRole(GESTORI_ARCHIVIO)
                .requestMatchers("/api/raccolte/**").hasAnyRole(CHI_COMPONE)

                // --- calendario e presenze ---
                .requestMatchers("/api/eventi/*/presenze/**").hasAnyRole(CALENDARI)
                .requestMatchers(GET, "/api/eventi/**").hasAnyRole(TUTTI)
                .requestMatchers("/api/eventi/**").hasAnyRole(CALENDARI)

                // --- tutto il resto è chiuso ---
                .requestMatchers("/api/**").denyAll()
                .anyRequest().denyAll();
    }
}