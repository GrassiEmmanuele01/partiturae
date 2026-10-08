package com.grassi.partiturae.seed;

import com.grassi.partiturae.model.Famiglia;
import com.grassi.partiturae.model.Strumento;
import com.grassi.partiturae.model.StrumentoFiglio;
import com.grassi.partiturae.repositories.FamigliaRepository;
import com.grassi.partiturae.repositories.StrumentoFiglioRepository;
import com.grassi.partiturae.repositories.StrumentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DefaultInstrumentsSeeder implements CommandLineRunner {

    private final FamigliaRepository famigliaRepository;
    private final StrumentoRepository strumentoRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;

    public DefaultInstrumentsSeeder(
            FamigliaRepository famigliaRepository,
            StrumentoRepository strumentoRepository,
            StrumentoFiglioRepository strumentoFiglioRepository) {

        this.famigliaRepository = famigliaRepository;
        this.strumentoRepository = strumentoRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Non sovrascrive dati già presenti nel database.
        if (famigliaRepository.count() > 0) {
            return;
        }

        seedLegni();
        seedOttoni();
        seedArchi();
        seedPercussioni();
        seedTastiereEPizzico();
    }

    // ============================================================
    // LEGNI
    // ============================================================

    private void seedLegni() {

        Famiglia legni = creaFamiglia("Legni");

        creaStrumento(
                legni,
                "Ottavino",
                "Ottavino 1",
                "Ottavino 2"
        );

        creaStrumento(
                legni,
                "Flauto",
                "Flauto 1",
                "Flauto 2",
                "Flauto 3"
        );

        creaStrumento(
                legni,
                "Flauto contralto",
                "Flauto contralto 1",
                "Flauto contralto 2"
        );

        creaStrumento(
                legni,
                "Flauto basso",
                "Flauto basso 1"
        );

        creaStrumento(
                legni,
                "Oboe",
                "Oboe 1",
                "Oboe 2",
                "Oboe 3"
        );

        creaStrumento(
                legni,
                "Oboe d'amore",
                "Oboe d'amore 1",
                "Oboe d'amore 2"
        );

        creaStrumento(
                legni,
                "Corno inglese",
                "Corno inglese 1",
                "Corno inglese 2"
        );

        creaStrumento(
                legni,
                "Heckelphon",
                "Heckelphon 1"
        );

        creaStrumento(
                legni,
                "Clarinetto piccolo",
                "Clarinetto piccolo in Mi bemolle",
                "Clarinetto piccolo in Re"
        );

        creaStrumento(
                legni,
                "Clarinetto soprano",
                "Clarinetto in Mi bemolle",
                "Clarinetto in Si bemolle 1",
                "Clarinetto in Si bemolle 2",
                "Clarinetto in Si bemolle 3",
                "Clarinetto in Si bemolle 4"
        );

        creaStrumento(
                legni,
                "Clarinetto di bassetto",
                "Clarinetto di bassetto 1"
        );

        creaStrumento(
                legni,
                "Corno di bassetto",
                "Corno di bassetto 1"
        );

        creaStrumento(
                legni,
                "Clarinetto contralto",
                "Clarinetto contralto 1"
        );

        creaStrumento(
                legni,
                "Clarinetto basso",
                "Clarinetto basso 1",
                "Clarinetto basso 2"
        );

        creaStrumento(
                legni,
                "Clarinetto contrabbasso",
                "Clarinetto contrabbasso 1"
        );

        creaStrumento(
                legni,
                "Fagotto",
                "Fagotto 1",
                "Fagotto 2",
                "Fagotto 3"
        );

        creaStrumento(
                legni,
                "Controfagotto",
                "Controfagotto 1",
                "Controfagotto 2"
        );

        creaStrumento(
                legni,
                "Sassofono soprano",
                "Sax soprano 1",
                "Sax soprano 2"
        );

        creaStrumento(
                legni,
                "Sassofono contralto",
                "Sax contralto 1",
                "Sax contralto 2",
                "Sax contralto 3"
        );

        creaStrumento(
                legni,
                "Sassofono tenore",
                "Sax tenore 1",
                "Sax tenore 2"
        );

        creaStrumento(
                legni,
                "Sassofono baritono",
                "Sax baritono 1",
                "Sax baritono 2"
        );

        creaStrumento(
                legni,
                "Sassofono basso",
                "Sax basso 1"
        );

        creaStrumento(
                legni,
                "Sassofono contrabbasso",
                "Sax contrabbasso 1"
        );
    }

    // ============================================================
    // OTTONI
    // ============================================================

    private void seedOttoni() {

        Famiglia ottoni = creaFamiglia("Ottoni");

        // Trombe

        creaStrumento(
                ottoni,
                "Tromba",
                "Tromba 1",
                "Tromba 2",
                "Tromba 3",
                "Tromba 4"
        );

        creaStrumento(
                ottoni,
                "Tromba in Do",
                "Tromba in Do 1",
                "Tromba in Do 2",
                "Tromba in Do 3"
        );

        creaStrumento(
                ottoni,
                "Tromba in Si bemolle",
                "Tromba in Si bemolle 1",
                "Tromba in Si bemolle 2",
                "Tromba in Si bemolle 3"
        );

        creaStrumento(
                ottoni,
                "Tromba piccola",
                "Tromba piccola 1",
                "Tromba piccola 2"
        );

        creaStrumento(
                ottoni,
                "Cornetta",
                "Cornetta 1",
                "Cornetta 2",
                "Cornetta 3"
        );

        // Corni

        creaStrumento(
                ottoni,
                "Corno",
                "Corno 1",
                "Corno 2",
                "Corno 3",
                "Corno 4",
                "Corno 5",
                "Corno 6"
        );

        // Flicorni

        creaStrumento(
                ottoni,
                "Flicorno soprano",
                "Flicorno soprano 1",
                "Flicorno soprano 2"
        );

        creaStrumento(
                ottoni,
                "Flicorno contralto",
                "Flicorno contralto 1",
                "Flicorno contralto 2"
        );

        creaStrumento(
                ottoni,
                "Flicorno tenore",
                "Flicorno tenore 1",
                "Flicorno tenore 2"
        );

        creaStrumento(
                ottoni,
                "Flicorno baritono",
                "Flicorno baritono 1",
                "Flicorno baritono 2"
        );

        creaStrumento(
                ottoni,
                "Flicorno basso",
                "Flicorno basso 1",
                "Flicorno basso 2"
        );

        // Euphonium

        creaStrumento(
                ottoni,
                "Eufonio",
                "Eufonio 1",
                "Eufonio 2"
        );

        // Tromboni

        creaStrumento(
                ottoni,
                "Trombone",
                "Trombone 1",
                "Trombone 2",
                "Trombone 3"
        );

        creaStrumento(
                ottoni,
                "Trombone tenore",
                "Trombone tenore 1",
                "Trombone tenore 2",
                "Trombone tenore 3"
        );

        creaStrumento(
                ottoni,
                "Trombone basso",
                "Trombone basso 1",
                "Trombone basso 2"
        );

        // Bassi

        creaStrumento(
                ottoni,
                "Tuba",
                "Tuba 1",
                "Tuba 2"
        );

        creaStrumento(
                ottoni,
                "Elicon",
                "Elicon 1",
                "Elicon 2"
        );

        creaStrumento(
                ottoni,
                "Sousafono",
                "Sousafono 1",
                "Sousafono 2"
        );

        creaStrumento(
                ottoni,
                "Basso tuba",
                "Basso tuba 1",
                "Basso tuba 2"
        );
    }

    // ============================================================
    // ARCHI
    // ============================================================

    private void seedArchi() {

        Famiglia archi = creaFamiglia("Archi");

        creaStrumento(
                archi,
                "Violino",
                "Violino 1",
                "Violino 2",
                "Violino 3",
                "Violino 4"
        );

        creaStrumento(
                archi,
                "Violino primo",
                "Violino primo 1",
                "Violino primo 2"
        );

        creaStrumento(
                archi,
                "Violino secondo",
                "Violino secondo 1",
                "Violino secondo 2"
        );

        creaStrumento(
                archi,
                "Viola",
                "Viola 1",
                "Viola 2",
                "Viola 3"
        );

        creaStrumento(
                archi,
                "Violoncello",
                "Violoncello 1",
                "Violoncello 2",
                "Violoncello 3"
        );

        creaStrumento(
                archi,
                "Contrabbasso",
                "Contrabbasso 1",
                "Contrabbasso 2",
                "Contrabbasso 3"
        );

        creaStrumento(
                archi,
                "Arpa",
                "Arpa 1",
                "Arpa 2"
        );

        creaStrumento(
                archi,
                "Mandolino",
                "Mandolino 1",
                "Mandolino 2"
        );

        creaStrumento(
                archi,
                "Chitarra",
                "Chitarra 1",
                "Chitarra 2"
        );
    }

    // ============================================================
    // PERCUSSIONI
    // ============================================================

    private void seedPercussioni() {

        Famiglia percussioni = creaFamiglia("Percussioni");

        creaStrumento(
                percussioni,
                "Timpani",
                "Timpani 1",
                "Timpani 2",
                "Timpani 3",
                "Timpani 4"
        );

        creaStrumento(
                percussioni,
                "Grancassa",
                "Grancassa orchestrale"
        );

        creaStrumento(
                percussioni,
                "Rullante",
                "Rullante orchestrale",
                "Rullante da formazione"
        );

        creaStrumento(
                percussioni,
                "Piatti",
                "Piatti a due",
                "Piatti sospesi"
        );

        creaStrumento(
                percussioni,
                "Triangolo",
                "Triangolo 1"
        );

        creaStrumento(
                percussioni,
                "Tamburello",
                "Tamburello 1"
        );

        creaStrumento(
                percussioni,
                "Tamburo militare",
                "Tamburo militare 1"
        );

        creaStrumento(
                percussioni,
                "Xilofono",
                "Xilofono 1"
        );

        creaStrumento(
                percussioni,
                "Marimba",
                "Marimba 1"
        );

        creaStrumento(
                percussioni,
                "Vibrafono",
                "Vibrafono 1"
        );

        creaStrumento(
                percussioni,
                "Glockenspiel",
                "Glockenspiel 1"
        );

        creaStrumento(
                percussioni,
                "Campane tubolari",
                "Campane tubolari 1"
        );

        creaStrumento(
                percussioni,
                "Celesta",
                "Celesta 1"
        );

        creaStrumento(
                percussioni,
                "Castagnette",
                "Castagnette 1"
        );

        creaStrumento(
                percussioni,
                "Woodblock",
                "Woodblock 1"
        );

        creaStrumento(
                percussioni,
                "Tam-tam",
                "Tam-tam 1"
        );

        creaStrumento(
                percussioni,
                "Batteria",
                "Batteria 1"
        );

        creaStrumento(
                percussioni,
                "Cassa",
                "Cassa 1"
        );

        creaStrumento(
                percussioni,
                "Crotali",
                "Crotali 1"
        );

        creaStrumento(
                percussioni,
                "Tamburo basco",
                "Tamburo basco 1"
        );

        creaStrumento(
                percussioni,
                "Agogò",
                "Agogò 1"
        );
    }

    // ============================================================
    // TASTIERE E PIZZICO
    // ============================================================

    private void seedTastiereEPizzico() {

        Famiglia tastiere = creaFamiglia("Tastiere e pizzico");

        creaStrumento(
                tastiere,
                "Pianoforte",
                "Pianoforte 1"
        );

        creaStrumento(
                tastiere,
                "Pianoforte a quattro mani",
                "Pianoforte a quattro mani 1"
        );

        creaStrumento(
                tastiere,
                "Organo",
                "Organo 1"
        );

        creaStrumento(
                tastiere,
                "Clavicembalo",
                "Clavicembalo 1"
        );

        creaStrumento(
                tastiere,
                "Celesta",
                "Celesta 1"
        );

        creaStrumento(
                tastiere,
                "Chitarra",
                "Chitarra 1",
                "Chitarra 2"
        );

        creaStrumento(
                tastiere,
                "Chitarra elettrica",
                "Chitarra elettrica 1",
                "Chitarra elettrica 2"
        );

        creaStrumento(
                tastiere,
                "Basso elettrico",
                "Basso elettrico 1",
                "Basso elettrico 2"
        );

        creaStrumento(
                tastiere,
                "Mandolino",
                "Mandolino 1",
                "Mandolino 2"
        );

        creaStrumento(
                tastiere,
                "Banjo",
                "Banjo 1"
        );

        creaStrumento(
                tastiere,
                "Ukulele",
                "Ukulele 1"
        );

        creaStrumento(
                tastiere,
                "Arpa",
                "Arpa 1",
                "Arpa 2"
        );
    }

    // ============================================================
    // CREAZIONE FAMIGLIA
    // ============================================================

    private Famiglia creaFamiglia(String nome) {

        return famigliaRepository.save(
                Famiglia.builder()
                        .nome(nome)
                        .build()
        );
    }

    private void creaStrumento(
            Famiglia famiglia,
            String nome,
            String... figli) {

        Strumento strumento = strumentoRepository.save(
                Strumento.builder()
                        .nome(nome)
                        .famiglia(famiglia)
                        .build()
        );

        List<String> nomiFigli = new ArrayList<>();
        nomiFigli.add(nome);

        if (figli != null) {
            nomiFigli.addAll(Arrays.asList(figli));
        }

        for (String nomeFiglio : nomiFigli) {

            if (nomeFiglio == null || nomeFiglio.isBlank()) {
                continue;
            }

            strumentoFiglioRepository.save(
                    StrumentoFiglio.builder()
                            .nome(nomeFiglio)
                            .strumento(strumento)
                            .build()
            );
        }
    }
}