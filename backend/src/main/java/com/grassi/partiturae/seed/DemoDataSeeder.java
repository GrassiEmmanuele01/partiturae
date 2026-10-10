package com.grassi.partiturae.seed;

import com.grassi.partiturae.auth.Account;
import com.grassi.partiturae.auth.AccountRepository;
import com.grassi.partiturae.auth.Appartenenza;
import com.grassi.partiturae.auth.AppartenenzaRepository;
import com.grassi.partiturae.auth.AuthProperties;
import com.grassi.partiturae.auth.Ruolo;
import com.grassi.partiturae.autore.Autore;
import com.grassi.partiturae.autore.AutoreRepository;
import com.grassi.partiturae.banda.Banda;
import com.grassi.partiturae.banda.BandaContext;
import com.grassi.partiturae.banda.BandaRepository;
import com.grassi.partiturae.musicista.Musicista;
import com.grassi.partiturae.musicista.MusicistaRepository;
import com.grassi.partiturae.parte.Parte;
import com.grassi.partiturae.parte.ParteDocumento;
import com.grassi.partiturae.parte.ParteRepository;
import com.grassi.partiturae.partitura.Partitura;
import com.grassi.partiturae.partitura.PartituraRepository;
import com.grassi.partiturae.partitura.TipoPartitura;
import com.grassi.partiturae.socio.Socio;
import com.grassi.partiturae.socio.SocioRepository;
import com.grassi.partiturae.strumento.Strumento;
import com.grassi.partiturae.strumento.StrumentoRepository;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dati di prova per lo sviluppo: un account per ogni ruolo, una seconda banda e qualche partitura con le parti.
 * Non fa niente se non lo si accende: si attiva avviando il backend con PARTITURAE_DEMO=true
 * e non parte mai con app.security.cookie-secure=true (cioè in produzione).
 *
 * Si può rilanciare quante volte si vuole: crea solo ciò che manca e non modifica quello che c'è già.
 * Tutti gli account di prova hanno la stessa password (PARTITURAE_DEMO_PASSWORD, di default Prova-2026).
 */
@Slf4j
@Component
@Order(3)
public class DemoDataSeeder implements CommandLineRunner {

    private static final String NOME_SECONDA_BANDA = "Banda di prova";
    private static final String SEGNALE_DEMO = "(demo)";

    /** Il ruolo (o i ruoli) di una persona in una banda: la prima o la seconda. */
    private record Incarico(boolean secondaBanda, Set<Ruolo> ruoli) {
    }

    private record Persona(String email, String nome, String cognome, String strumento, List<Incarico> incarichi) {
    }

    private static Incarico nellaPrima(Ruolo... ruoli) {
        return new Incarico(false, Set.of(ruoli));
    }

    private static Incarico nellaSeconda(Ruolo... ruoli) {
        return new Incarico(true, Set.of(ruoli));
    }

    private static final List<Persona> PERSONE = List.of(
            new Persona("archivista@prova.it", "Anna", "Archivi", null, List.of(nellaPrima(Ruolo.ARCHIVISTA))),
            new Persona("maestro@prova.it", "Marco", "Maestri", null, List.of(nellaPrima(Ruolo.MAESTRO))),
            new Persona("maestroallievi@prova.it", "Giulia", "Giovani", null, List.of(nellaPrima(Ruolo.MAESTROALLIEVI))),
            new Persona("direttivo@prova.it", "Dario", "Direttivi", null, List.of(nellaPrima(Ruolo.DIRETTIVO))),
            new Persona("musicista@prova.it", "Mario", "Trombetta", "Tromba", List.of(nellaPrima(Ruolo.MUSICISTA))),
            new Persona("allievo@prova.it", "Alice", "Clarini", "Clarinetto soprano", List.of(nellaPrima(Ruolo.ALLIEVO))),
            new Persona("socio@prova.it", "Sara", "Socia", null, List.of(nellaPrima(Ruolo.SOCIO))),
            // due ruoli nella prima banda e un ruolo diverso nella seconda: per provare il cambio di banda
            new Persona("multi@prova.it", "Luca", "Multiruolo", "Tromba",
                    List.of(nellaPrima(Ruolo.DIRETTIVO, Ruolo.MAESTRO), nellaSeconda(Ruolo.MUSICISTA))),
            new Persona("admin2@prova.it", "Elena", "Seconda", null, List.of(nellaSeconda(Ruolo.ADMIN))));

    private final boolean abilitato;
    private final String password;
    private final AuthProperties authProperties;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager transactionManager;
    private final DefaultInstrumentsSeeder strumentiDiPartenza;
    private final BandaRepository bandaRepository;
    private final AccountRepository accountRepository;
    private final AppartenenzaRepository appartenenzaRepository;
    private final SocioRepository socioRepository;
    private final MusicistaRepository musicistaRepository;
    private final StrumentoRepository strumentoRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final AutoreRepository autoreRepository;
    private final PartituraRepository partituraRepository;
    private final ParteRepository parteRepository;

    public DemoDataSeeder(@Value("${app.demo.enabled:false}") boolean abilitato,
                           @Value("${app.demo.password:Prova-2026}") String password,
                           AuthProperties authProperties,
                           PasswordEncoder passwordEncoder,
                           PlatformTransactionManager transactionManager,
                           DefaultInstrumentsSeeder strumentiDiPartenza,
                           BandaRepository bandaRepository,
                           AccountRepository accountRepository,
                           AppartenenzaRepository appartenenzaRepository,
                           SocioRepository socioRepository,
                           MusicistaRepository musicistaRepository,
                           StrumentoRepository strumentoRepository,
                           StrumentoFiglioRepository strumentoFiglioRepository,
                           AutoreRepository autoreRepository,
                           PartituraRepository partituraRepository,
                           ParteRepository parteRepository) {
        this.abilitato = abilitato;
        this.password = password;
        this.authProperties = authProperties;
        this.passwordEncoder = passwordEncoder;
        this.transactionManager = transactionManager;
        this.strumentiDiPartenza = strumentiDiPartenza;
        this.bandaRepository = bandaRepository;
        this.accountRepository = accountRepository;
        this.appartenenzaRepository = appartenenzaRepository;
        this.socioRepository = socioRepository;
        this.musicistaRepository = musicistaRepository;
        this.strumentoRepository = strumentoRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.autoreRepository = autoreRepository;
        this.partituraRepository = partituraRepository;
        this.parteRepository = parteRepository;
    }

    @Override
    public void run(String... args) {
        if (!abilitato) {
            return;
        }

        if (authProperties.cookieSecure()) {
            throw new IllegalStateException(
                    "I dati di prova sono solo per lo sviluppo e non partono con app.security.cookie-secure=true.");
        }

        List<Banda> bande = bandaRepository.findAll();
        Banda prima = bande.stream().min(Comparator.comparing(Banda::getId)).orElse(null);

        if (prima == null) {
            log.warn("Non c'è nessuna banda: i dati di prova non sono stati creati.");
            return;
        }

        String nomeSeconda = NOME_SECONDA_BANDA.equalsIgnoreCase(prima.getNome())
                ? "Seconda banda di prova"
                : NOME_SECONDA_BANDA;

        Banda seconda = bande.stream()
                .filter(banda -> nomeSeconda.equals(banda.getNome()))
                .findFirst()
                .orElseGet(() -> bandaRepository.save(Banda.builder().nome(nomeSeconda).build()));

        // La seconda banda ha il suo catalogo di strumenti, come ogni banda.
        strumentiDiPartenza.inizializzaCatalogo(seconda.getId());

        creaAccount(prima, seconda);
        creaDatiDellaBanda(prima, false);
        creaDatiDellaBanda(seconda, true);

        mostraRiepilogo(prima, seconda);
    }

    /** Account e ruoli: dati della piattaforma, non appartengono a nessuna banda. */
    private void creaAccount(Banda prima, Banda seconda) {
        new TransactionTemplate(transactionManager).executeWithoutResult(stato -> {
            for (Persona persona : PERSONE) {
                Account account = accountRepository.findByEmailIgnoreCase(persona.email())
                        .orElseGet(() -> accountRepository.save(Account.builder()
                                .email(persona.email())
                                .passwordHash(passwordEncoder.encode(password))
                                .nome(persona.nome())
                                .cognome(persona.cognome())
                                .ultimaBandaId(prima.getId())
                                .build()));

                for (Incarico incarico : persona.incarichi()) {
                    Banda banda = incarico.secondaBanda() ? seconda : prima;

                    if (appartenenzaRepository.findByAccountIdAndBandaId(account.getId(), banda.getId()).isEmpty()) {
                        appartenenzaRepository.save(Appartenenza.builder()
                                .account(account)
                                .banda(banda)
                                .ruoli(new HashSet<>(incarico.ruoli()))
                                .build());
                    }
                }
            }
        });
    }

    /** Soci, profili musicali, partiture e parti di una banda: si creano dentro quella banda. */
    private void creaDatiDellaBanda(Banda banda, boolean secondaBanda) {
        BandaContext.esegui(banda.getId(), () ->
                new TransactionTemplate(transactionManager).executeWithoutResult(stato -> {
                    creaSociEProfili(secondaBanda);
                    creaPartiture(secondaBanda);
                }));
    }

    private void creaSociEProfili(boolean secondaBanda) {
        List<Strumento> strumenti = strumentoRepository.findAll();

        for (Persona persona : PERSONE) {
            Optional<Incarico> incarico = persona.incarichi().stream()
                    .filter(i -> i.secondaBanda() == secondaBanda)
                    .findFirst();

            if (incarico.isEmpty()) {
                continue;
            }

            // Il socio ha la stessa email dell'account: è così che l'account trova le sue parti.
            Socio socio = socioRepository.findByMailIgnoreCase(persona.email())
                    .orElseGet(() -> socioRepository.save(Socio.builder()
                            .nome(persona.nome())
                            .cognome(persona.cognome())
                            .mail(persona.email())
                            .build()));

            boolean suona = incarico.get().ruoli().contains(Ruolo.MUSICISTA)
                    || incarico.get().ruoli().contains(Ruolo.ALLIEVO);

            if (suona && persona.strumento() != null && musicistaRepository.findBySocioId(socio.getId()).isEmpty()) {
                Optional<Strumento> strumento = strumenti.stream()
                        .filter(s -> persona.strumento().equals(s.getNome()))
                        .findFirst();

                if (strumento.isPresent()) {
                    musicistaRepository.save(Musicista.builder()
                            .socio(socio)
                            .strumenti(new HashSet<>(Set.of(strumento.get())))
                            .build());
                } else {
                    log.warn("Lo strumento \"{}\" non è nel catalogo: {} non ha il profilo musicale.",
                            persona.strumento(), persona.email());
                }
            }
        }
    }

    private void creaPartiture(boolean secondaBanda) {
        if (!partituraRepository.findByNomeContainingIgnoreCase(SEGNALE_DEMO).isEmpty()) {
            return; // già create
        }

        Map<String, StrumentoFiglio> voci = strumentoFiglioRepository.findAll().stream()
                .collect(Collectors.toMap(StrumentoFiglio::getNome, voce -> voce, (a, b) -> a));

        if (!secondaBanda) {
            Autore mameli = autoreRepository.save(Autore.builder().nominativo("Goffredo Mameli").build());
            Autore autoreDiProva = autoreRepository.save(Autore.builder().nominativo("Autore di prova").build());

            Partitura inno = partituraRepository.save(Partitura.builder()
                    .nome("Inno di Mameli " + SEGNALE_DEMO)
                    .descrizione("Partitura di prova creata dai dati di prova.")
                    .anno(1847)
                    .tipo(TipoPartitura.INNO)
                    .autore(mameli)
                    .build());
            creaParte(inno, voci, true, "Tromba 1", "Tromba 2");
            creaParte(inno, voci, true, "Clarinetto in Si bemolle 1");
            creaParte(inno, voci, false, "Flauto 1");

            Partitura marcia = partituraRepository.save(Partitura.builder()
                    .nome("Marcia lenta " + SEGNALE_DEMO)
                    .descrizione("Seconda partitura di prova, con una parte senza PDF.")
                    .tipo(TipoPartitura.MARCIA_CONCERTO)
                    .autore(autoreDiProva)
                    .build());
            creaParte(marcia, voci, false, "Tromba 3");
            creaParte(marcia, voci, true, "Clarinetto in Si bemolle 2");
        } else {
            Autore autore = autoreRepository.save(Autore.builder().nominativo("Autore della banda di prova").build());

            Partitura marcia = partituraRepository.save(Partitura.builder()
                    .nome("Marcia della banda di prova " + SEGNALE_DEMO)
                    .descrizione("Si vede solo nella seconda banda: serve a provare la separazione dei dati.")
                    .tipo(TipoPartitura.MARCIA_LIBRETTO)
                    .autore(autore)
                    .build());
            creaParte(marcia, voci, true, "Tromba 1");
        }
    }

    private void creaParte(Partitura partitura, Map<String, StrumentoFiglio> voci, boolean conPdf, String... nomiVoci) {
        Set<StrumentoFiglio> strumenti = new HashSet<>();

        for (String nome : nomiVoci) {
            StrumentoFiglio voce = voci.get(nome);
            if (voce == null) {
                log.warn("La voce \"{}\" non è nel catalogo: la parte di prova non è stata creata.", nome);
                return;
            }
            strumenti.add(voce);
        }

        Parte parte = Parte.builder().partitura(partitura).strumenti(strumenti).build();

        if (conPdf) {
            ParteDocumento documento = new ParteDocumento();
            documento.setContenuto(PdfDiProva.crea(
                    partitura.getNome(), "Parte: " + String.join(", ", nomiVoci) + " (PDF di prova)"));
            parte.setDocumento(documento);
        }

        parteRepository.save(parte);
    }

    private void mostraRiepilogo(Banda prima, Banda seconda) {
        StringBuilder testo = new StringBuilder("\n")
                .append("==================================================================\n")
                .append("  DATI DI PROVA ATTIVI (solo per lo sviluppo)\n")
                .append("  Password di tutti gli account di prova: ").append(password).append("\n");

        aggiungiAccount(testo, prima, false);
        aggiungiAccount(testo, seconda, true);

        testo.append("==================================================================\n");
        log.warn(testo.toString());
    }

    private void aggiungiAccount(StringBuilder testo, Banda banda, boolean secondaBanda) {
        testo.append("  Banda \"").append(banda.getNome()).append("\":\n");

        for (Persona persona : PERSONE) {
            for (Incarico incarico : persona.incarichi()) {
                if (incarico.secondaBanda() == secondaBanda) {
                    String ruoli = incarico.ruoli().stream().map(Ruolo::name).sorted().collect(Collectors.joining(", "));
                    testo.append("    ").append(persona.email()).append("  ->  ").append(ruoli).append("\n");
                }
            }
        }
    }
}