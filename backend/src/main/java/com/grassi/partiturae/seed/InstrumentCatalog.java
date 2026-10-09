package com.grassi.partiturae.seed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Strumenti di partenza di una formazione bandistica. Contiene solo dati: per aggiungere,
 * togliere o rinominare uno strumento basta modificare questo file.
 *
 * Ogni strumento ha sempre una voce con il suo stesso nome (es. "Ottavino") più le voci
 * elencate (es. "Ottavino 1", "Ottavino 2"), così una parte può riferirsi anche allo strumento
 * senza numero.
 */
public final class InstrumentCatalog {

    /** Una famiglia con i suoi strumenti. */
    public record FamigliaDef(String nome, List<StrumentoDef> strumenti) {
    }

    /** Uno strumento con tutte le sue voci (la prima ha lo stesso nome dello strumento). */
    public record StrumentoDef(String nome, List<String> voci) {
    }

    private InstrumentCatalog() {
    }

    public static List<FamigliaDef> defaults() {
        return List.of(legni(), ottoni(), archi(), percussioni(), tastiereEPizzico());
    }

    // ------------------------------------------------------------------
    // Come si scrivono gli strumenti
    //   numerato("Flauto", 3)                      -> Flauto, Flauto 1, Flauto 2, Flauto 3
    //   numerato("Sassofono tenore", "Sax tenore", 2) -> Sassofono tenore, Sax tenore 1, Sax tenore 2
    //   strumento("Piatti", "Piatti a due", "...")    -> Piatti, Piatti a due, ...
    // ------------------------------------------------------------------

    private static FamigliaDef legni() {
        return new FamigliaDef("Legni", List.of(
                numerato("Ottavino", 2),
                numerato("Flauto", 3),
                numerato("Flauto contralto", 2),
                numerato("Flauto basso", 1),
                numerato("Oboe", 3),
                numerato("Oboe d'amore", 2),
                numerato("Corno inglese", 2),
                numerato("Heckelphon", 1),
                strumento("Clarinetto piccolo",
                        "Clarinetto piccolo in Mi bemolle",
                        "Clarinetto piccolo in Re"),
                strumento("Clarinetto soprano",
                        "Clarinetto in Mi bemolle",
                        "Clarinetto in Si bemolle 1",
                        "Clarinetto in Si bemolle 2",
                        "Clarinetto in Si bemolle 3",
                        "Clarinetto in Si bemolle 4"),
                numerato("Clarinetto di bassetto", 1),
                numerato("Corno di bassetto", 1),
                numerato("Clarinetto contralto", 1),
                numerato("Clarinetto basso", 2),
                numerato("Clarinetto contrabbasso", 1),
                numerato("Fagotto", 3),
                numerato("Controfagotto", 2),
                numerato("Sassofono soprano", "Sax soprano", 2),
                numerato("Sassofono contralto", "Sax contralto", 3),
                numerato("Sassofono tenore", "Sax tenore", 2),
                numerato("Sassofono baritono", "Sax baritono", 2),
                numerato("Sassofono basso", "Sax basso", 1),
                numerato("Sassofono contrabbasso", "Sax contrabbasso", 1)
        ));
    }

    private static FamigliaDef ottoni() {
        return new FamigliaDef("Ottoni", List.of(
                // Trombe
                numerato("Tromba", 4),
                numerato("Tromba in Do", 3),
                numerato("Tromba in Si bemolle", 3),
                numerato("Tromba piccola", 2),
                numerato("Cornetta", 3),
                // Corni
                numerato("Corno", 6),
                // Flicorni
                numerato("Flicorno soprano", 2),
                numerato("Flicorno contralto", 2),
                numerato("Flicorno tenore", 2),
                numerato("Flicorno baritono", 2),
                numerato("Flicorno basso", 2),
                // Eufonio
                numerato("Eufonio", 2),
                // Tromboni
                numerato("Trombone", 3),
                numerato("Trombone tenore", 3),
                numerato("Trombone basso", 2),
                // Bassi
                numerato("Tuba", 2),
                numerato("Elicon", 2),
                numerato("Sousafono", 2),
                numerato("Basso tuba", 2)
        ));
    }

    /** Solo archi ad arco: chitarra, mandolino e arpa stanno in "Tastiere e pizzico". */
    private static FamigliaDef archi() {
        return new FamigliaDef("Archi", List.of(
                numerato("Violino", 4),
                numerato("Violino primo", 2),
                numerato("Violino secondo", 2),
                numerato("Viola", 3),
                numerato("Violoncello", 3),
                numerato("Contrabbasso", 3)
        ));
    }

    private static FamigliaDef percussioni() {
        return new FamigliaDef("Percussioni", List.of(
                numerato("Timpani", 4),
                strumento("Grancassa", "Grancassa orchestrale"),
                strumento("Rullante", "Rullante orchestrale", "Rullante da formazione"),
                strumento("Piatti", "Piatti a due", "Piatti sospesi"),
                numerato("Triangolo", 1),
                numerato("Tamburello", 1),
                numerato("Tamburo militare", 1),
                numerato("Xilofono", 1),
                numerato("Marimba", 1),
                numerato("Vibrafono", 1),
                numerato("Glockenspiel", 1),
                numerato("Campane tubolari", 1),
                numerato("Celesta", 1),
                numerato("Castagnette", 1),
                numerato("Woodblock", 1),
                numerato("Tam-tam", 1),
                numerato("Batteria", 1),
                numerato("Cassa", 1),
                numerato("Crotali", 1),
                numerato("Tamburo basco", 1),
                numerato("Agogò", 1)
        ));
    }

    private static FamigliaDef tastiereEPizzico() {
        return new FamigliaDef("Tastiere e pizzico", List.of(
                numerato("Pianoforte", 1),
                numerato("Pianoforte a quattro mani", 1),
                numerato("Organo", 1),
                numerato("Clavicembalo", 1),
                numerato("Chitarra", 2),
                numerato("Chitarra elettrica", 2),
                numerato("Basso elettrico", 2),
                numerato("Mandolino", 2),
                numerato("Banjo", 1),
                numerato("Ukulele", 1),
                numerato("Arpa", 2)
        ));
    }

    // ------------------------------------------------------------------
    // Utilità
    // ------------------------------------------------------------------

    /** Strumento con voci scritte a mano. */
    private static StrumentoDef strumento(String nome, String... voci) {
        List<String> tutte = new ArrayList<>();
        tutte.add(nome);
        tutte.addAll(Arrays.asList(voci));
        return new StrumentoDef(nome, List.copyOf(tutte));
    }

    /** Strumento con voci numerate che usano il nome dello strumento: "Flauto 1", "Flauto 2", ... */
    private static StrumentoDef numerato(String nome, int quante) {
        return numerato(nome, nome, quante);
    }

    /** Strumento con voci numerate che usano un prefisso diverso (es. "Sassofono tenore" -> "Sax tenore 1"). */
    private static StrumentoDef numerato(String nome, String prefissoVoci, int quante) {
        List<String> tutte = new ArrayList<>();
        tutte.add(nome);
        for (int i = 1; i <= quante; i++) {
            tutte.add(prefissoVoci + " " + i);
        }
        return new StrumentoDef(nome, List.copyOf(tutte));
    }
}