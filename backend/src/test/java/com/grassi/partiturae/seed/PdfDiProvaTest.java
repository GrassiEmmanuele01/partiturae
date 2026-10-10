package com.grassi.partiturae.seed;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfDiProvaTest {

    private static String pdf(String titolo, String sottotitolo) {
        return new String(PdfDiProva.crea(titolo, sottotitolo), StandardCharsets.US_ASCII);
    }

    @Test
    void ilFileHaLaFormaDiUnPdfEContieneIlTesto() {
        String contenuto = pdf("Inno di Mameli (demo)", "Parte: Tromba 1");

        assertTrue(contenuto.startsWith("%PDF-1.4\n"));
        assertTrue(contenuto.endsWith("%%EOF\n"));
        assertTrue(contenuto.contains("Parte: Tromba 1"));
    }

    @Test
    void laTabellaFinalePuntaAgliOggettiEAllaTabellaStessa() {
        String contenuto = pdf("Titolo", "Sottotitolo");

        Matcher inizio = Pattern.compile("startxref\n(\\d+)\n").matcher(contenuto);
        assertTrue(inizio.find());
        assertTrue(contenuto.startsWith("xref", Integer.parseInt(inizio.group(1))));

        Matcher voci = Pattern.compile("(\\d{10}) 00000 n").matcher(contenuto);
        int numero = 0;
        while (voci.find()) {
            numero++;
            assertTrue(contenuto.startsWith(numero + " 0 obj", Integer.parseInt(voci.group(1))),
                    "l'oggetto " + numero + " non è dove dice la tabella");
        }
        assertEquals(5, numero);
    }

    @Test
    void parentesiEBackslashNonRomponoIlFileEICaratteriStraniDiventanoPuntiInterrogativi() {
        String contenuto = pdf("Marcia (lenta) \\ \u00e8", "ok");

        assertTrue(contenuto.contains("Marcia \\(lenta\\) \\\\ ?"));
        assertFalse(contenuto.contains("\u00e8"));
    }
}