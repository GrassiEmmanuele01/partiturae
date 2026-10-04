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

    private void seedLegni() {
        Famiglia legni = creaFamiglia("Legni");

        // Orchestra sinfonica e banda musicale
        creaStrumento(legni, "Ottavino",
                List.of("Ottavino 1", "Ottavino 2"));

        creaStrumento(legni, "Flauto",
                List.of("Flauto 1", "Flauto 2", "Flauto 3"));

        creaStrumento(legni, "Flauto contralto",
                List.of("Flauto contralto 1", "Flauto contralto 2"));

        creaStrumento(legni, "Oboe",
                List.of("Oboe 1", "Oboe 2", "Oboe 3"));

        creaStrumento(legni, "Corno inglese",
                List.of("Corno inglese 1"));

        creaStrumento(legni, "Clarinetto piccolo",
                List.of("Clarinetto piccolo in Mi bemolle"));

        creaStrumento(legni, "Clarinetto soprano",
                List.of(
                        "Clarinetto in Mi bemolle",
                        "Clarinetto in Si bemolle 1",
                        "Clarinetto in Si bemolle 2",
                        "Clarinetto in Si bemolle 3",
                        "Clarinetto in Si bemolle 4"
                ));

        creaStrumento(legni, "Clarinetto contralto",
                List.of("Clarinetto contralto 1"));

        creaStrumento(legni, "Clarinetto basso",
                List.of("Clarinetto basso 1", "Clarinetto basso 2"));

        creaStrumento(legni, "Clarinetto contrabbasso",
                List.of("Clarinetto contrabbasso 1"));

        creaStrumento(legni, "Fagotto",
                List.of("Fagotto 1", "Fagotto 2", "Fagotto 3"));

        creaStrumento(legni, "Controfagotto",
                List.of("Controfagotto 1"));

        creaStrumento(legni, "Sassofono soprano",
                List.of("Sax soprano 1", "Sax soprano 2"));

        creaStrumento(legni, "Sassofono contralto",
                List.of("Sax contralto 1", "Sax contralto 2", "Sax contralto 3"));

        creaStrumento(legni, "Sassofono tenore",
                List.of("Sax tenore 1", "Sax tenore 2"));

        creaStrumento(legni, "Sassofono baritono",
                List.of("Sax baritono 1", "Sax baritono 2"));

        creaStrumento(legni, "Sassofono basso",
                List.of("Sax basso 1"));
    }

    private void seedOttoni() {
        Famiglia ottoni = creaFamiglia("Ottoni");

        // Trombe
        creaStrumento(ottoni, "Tromba",
                List.of("Tromba 1", "Tromba 2", "Tromba 3", "Tromba 4"));

        creaStrumento(ottoni, "Cornetta",
                List.of("Cornetta 1", "Cornetta 2", "Cornetta 3"));

        // Corni
        creaStrumento(ottoni, "Corno",
                List.of("Corno 1", "Corno 2", "Corno 3", "Corno 4", "Corno 5", "Corno 6"));

        // Flicorni e strumenti affini, tipici soprattutto delle bande
        creaStrumento(ottoni, "Flicorno soprano",
                List.of("Flicorno soprano 1", "Flicorno soprano 2"));

        creaStrumento(ottoni, "Flicorno contralto",
                List.of("Flicorno contralto 1", "Flicorno contralto 2"));

        creaStrumento(ottoni, "Flicorno tenore",
                List.of("Flicorno tenore 1", "Flicorno tenore 2"));

        creaStrumento(ottoni, "Flicorno baritono",
                List.of("Flicorno baritono 1", "Flicorno baritono 2"));

        creaStrumento(ottoni, "Flicorno basso",
                List.of("Flicorno basso 1", "Flicorno basso 2"));

        creaStrumento(ottoni, "Eufonio",
                List.of("Eufonio 1", "Eufonio 2"));

        // Tromboni
        creaStrumento(ottoni, "Trombone tenore",
                List.of("Trombone 1", "Trombone 2", "Trombone 3"));

        creaStrumento(ottoni, "Trombone basso",
                List.of("Trombone basso 1", "Trombone basso 2"));

        // Bassi
        creaStrumento(ottoni, "Tuba",
                List.of("Tuba 1", "Tuba 2"));

        creaStrumento(ottoni, "Sousafono",
                List.of("Sousafono 1", "Sousafono 2"));

        creaStrumento(ottoni, "Basso tuba",
                List.of("Basso tuba 1", "Basso tuba 2"));
    }

    private void seedArchi() {
        Famiglia archi = creaFamiglia("Archi");

        // Sezione degli archi dell'orchestra sinfonica
        creaStrumento(archi, "Violino primo",
                List.of("Violino primo 1", "Violino primo 2"));

        creaStrumento(archi, "Violino secondo",
                List.of("Violino secondo 1", "Violino secondo 2"));

        creaStrumento(archi, "Viola",
                List.of("Viola 1", "Viola 2"));

        creaStrumento(archi, "Violoncello",
                List.of("Violoncello 1", "Violoncello 2"));

        creaStrumento(archi, "Contrabbasso",
                List.of("Contrabbasso 1", "Contrabbasso 2"));

        // Strumenti ad arco aggiuntivi, usati in organici particolari
        creaStrumento(archi, "Arpa",
                List.of("Arpa 1", "Arpa 2"));
    }

    private void seedPercussioni() {
        Famiglia percussioni = creaFamiglia("Percussioni");

        // Percussioni orchestrali
        creaStrumento(percussioni, "Timpani",
                List.of("Timpani 1", "Timpani 2", "Timpani 3", "Timpani 4"));

        creaStrumento(percussioni, "Grancassa",
                List.of("Grancassa orchestrale"));

        creaStrumento(percussioni, "Rullante",
                List.of("Rullante orchestrale", "Rullante da banda"));

        creaStrumento(percussioni, "Piatti",
                List.of("Piatti a due", "Piatti sospesi"));

        creaStrumento(percussioni, "Triangolo",
                List.of("Triangolo 1"));

        creaStrumento(percussioni, "Tamburello",
                List.of("Tamburello 1"));

        creaStrumento(percussioni, "Tamburo militare",
                List.of("Tamburo militare 1"));

        creaStrumento(percussioni, "Xilofono",
                List.of("Xilofono 1"));

        creaStrumento(percussioni, "Marimba",
                List.of("Marimba 1"));

        creaStrumento(percussioni, "Vibrafono",
                List.of("Vibrafono 1"));

        creaStrumento(percussioni, "Glockenspiel",
                List.of("Glockenspiel 1"));

        creaStrumento(percussioni, "Campane tubolari",
                List.of("Campane tubolari 1"));

        creaStrumento(percussioni, "Celesta",
                List.of("Celesta 1"));

        creaStrumento(percussioni, "Castagnette",
                List.of("Castagnette 1"));

        creaStrumento(percussioni, "Woodblock",
                List.of("Woodblock 1"));

        creaStrumento(percussioni, "Tam-tam",
                List.of("Tam-tam 1"));

        creaStrumento(percussioni, "Batteria",
                List.of("Batteria 1"));
    }

    private void seedTastiereEPizzico() {
        Famiglia tastiere = creaFamiglia("Tastiere e pizzico");

        creaStrumento(tastiere, "Pianoforte",
                List.of("Pianoforte 1"));

        creaStrumento(tastiere, "Organo",
                List.of("Organo 1"));

        creaStrumento(tastiere, "Clavicembalo",
                List.of("Clavicembalo 1"));

        creaStrumento(tastiere, "Chitarra",
                List.of("Chitarra 1", "Chitarra 2"));

        creaStrumento(tastiere, "Mandolino",
                List.of("Mandolino 1", "Mandolino 2"));

        creaStrumento(tastiere, "Banjo",
                List.of("Banjo 1"));
    }

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
            List<String> figli) {

        Strumento strumento = strumentoRepository.save(
                Strumento.builder()
                        .nome(nome)
                        .famiglia(famiglia)
                        .build()
        );

        for (String nomeFiglio : figli) {
            strumentoFiglioRepository.save(
                    StrumentoFiglio.builder()
                            .nome(nomeFiglio)
                            .strumento(strumento)
                            .build()
            );
        }
    }
}
