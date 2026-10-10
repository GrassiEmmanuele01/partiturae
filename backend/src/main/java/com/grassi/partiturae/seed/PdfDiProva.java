package com.grassi.partiturae.seed;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Crea un piccolo PDF valido (una pagina con due righe di testo) per le parti dei dati di prova,
 * così si può provare davvero il download senza dover caricare file.
 */
final class PdfDiProva {

    private PdfDiProva() {
    }

    static byte[] crea(String titolo, String sottotitolo) {
        String contenuto = "BT /F1 20 Tf 30 150 Td (" + testo(titolo) + ") Tj "
                + "/F1 12 Tf 0 -30 Td (" + testo(sottotitolo) + ") Tj ET";

        List<String> oggetti = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 420 200] /Contents 4 0 R "
                        + "/Resources << /Font << /F1 5 0 R >> >> >>",
                "<< /Length " + contenuto.length() + " >>\nstream\n" + contenuto + "\nendstream",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> posizioni = new ArrayList<>();

        for (int i = 0; i < oggetti.size(); i++) {
            posizioni.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n").append(oggetti.get(i)).append("\nendobj\n");
        }

        int inizioTabella = pdf.length();
        pdf.append("xref\n0 ").append(oggetti.size() + 1).append("\n0000000000 65535 f \n");
        for (int posizione : posizioni) {
            pdf.append(String.format(Locale.ROOT, "%010d 00000 n \n", posizione));
        }
        pdf.append("trailer\n<< /Size ").append(oggetti.size() + 1).append(" /Root 1 0 R >>\n")
                .append("startxref\n").append(inizioTabella).append("\n%%EOF\n");

        return pdf.toString().getBytes(StandardCharsets.US_ASCII);
    }

    /** Solo caratteri semplici: parentesi e backslash si proteggono, il resto che non è ASCII diventa "?". */
    private static String testo(String valore) {
        StringBuilder sb = new StringBuilder();
        for (char c : valore.toCharArray()) {
            if (c == '(' || c == ')' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c >= 32 && c < 127) {
                sb.append(c);
            } else {
                sb.append('?');
            }
        }
        return sb.toString();
    }
}