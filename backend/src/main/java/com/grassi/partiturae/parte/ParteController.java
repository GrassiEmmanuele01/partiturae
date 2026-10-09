package com.grassi.partiturae.parte;

import com.grassi.partiturae.common.dto.FileDownloadResponse;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/parti")
public class ParteController {

    private final ParteService parteService;

    public ParteController(ParteService parteService) {
        this.parteService = parteService;
    }

    @GetMapping
    public ResponseEntity<List<ParteResponse>> getAll(
            @RequestParam(required = false) Long partituraId,
            @RequestParam(required = false) Long strumentoFiglioId,
            @RequestParam(required = false) Long strumentoId) {
        if (partituraId != null) {
            return ResponseEntity.ok(parteService.getByPartitura(partituraId));
        }
        if (strumentoFiglioId != null) {
            return ResponseEntity.ok(parteService.getByStrumentoFiglio(strumentoFiglioId));
        }
        if (strumentoId != null) {
            return ResponseEntity.ok(parteService.getByStrumento(strumentoId));
        }
        throw new IllegalArgumentException("Specifica partituraId, strumentoFiglioId oppure strumentoId.");
    }

    @PostMapping
    public ResponseEntity<ParteResponse> create(@Valid @RequestBody ParteRequest request) {
        return ResponseEntity.status(201).body(parteService.create(request));
    }

    @PutMapping("/{id}/strumenti")
    public ResponseEntity<ParteResponse> updateStrumenti(@PathVariable Long id, @Valid @RequestBody ParteStrumentiRequest request) {
        return ResponseEntity.ok(parteService.updateStrumenti(id, request.getStrumentoFiglioIds()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPdf(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        parteService.uploadPdf(id, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        FileDownloadResponse file = parteService.getPdf(id);
        ContentDisposition disposition = ContentDisposition.inline().filename(file.filename()).build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.content());
    }
}
