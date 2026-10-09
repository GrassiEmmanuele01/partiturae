package com.grassi.partiturae.parte;

import java.text.Normalizer;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Nome standard dei PDF: Strumento_NomePartitura.pdf, senza spazi né simboli
 * (es. Ottavino1_InnoDiMameli.pdf). Se il PDF vale per più strumenti i nomi
 * vengono uniti con un trattino (es. CornoInFa1-CornoInFa2_Titolo.pdf).
 */
public final class PdfFileNames {

    private PdfFileNames() {
    }

    public static String forParte(String partituraNome, Collection<String> strumentiNomi) {
        String strumenti = strumentiNomi.stream()
                .map(PdfFileNames::pascalCase)
                .filter(nome -> !nome.isEmpty())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining("-"));
        String titolo = pascalCase(partituraNome);

        if (strumenti.isEmpty()) {
            strumenti = "Parte";
        }
        if (titolo.isEmpty()) {
            titolo = "Partitura";
        }

        return strumenti + "_" + titolo + ".pdf";
    }

    public static String pascalCase(String testo) {
        if (testo == null) {
            return "";
        }

        String senzaAccenti = Normalizer.normalize(testo, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        StringBuilder risultato = new StringBuilder();

        for (String parola : senzaAccenti.split("[^A-Za-z0-9]+")) {
            if (parola.isEmpty()) {
                continue;
            }
            risultato.append(Character.toUpperCase(parola.charAt(0))).append(parola.substring(1));
        }

        return risultato.toString();
    }
}
