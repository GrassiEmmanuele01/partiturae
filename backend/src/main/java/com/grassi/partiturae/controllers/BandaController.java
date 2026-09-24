package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.BandaRequest;
import com.grassi.partiturae.dto.BandaResponse;
import com.grassi.partiturae.dto.FileDownloadResponse;
import com.grassi.partiturae.services.BandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/banda")
public class BandaController {

    private final BandaService bandaService;

    public BandaController(BandaService bandaService) {
        this.bandaService = bandaService;
    }

    @GetMapping
    public ResponseEntity<BandaResponse> get() {
        return ResponseEntity.ok(bandaService.getBanda());
    }

    @PutMapping
    public ResponseEntity<BandaResponse> save(@Valid @RequestBody BandaRequest request) {
        return ResponseEntity.ok(bandaService.createOrUpdate(request));
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        bandaService.uploadLogo(file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logo")
    public ResponseEntity<byte[]> downloadLogo() {
        FileDownloadResponse file = bandaService.getLogo();
        MediaType mediaType = resolveImageMediaType(file.filename());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.filename() + "\"")
                .body(file.content());
    }

    private MediaType resolveImageMediaType(String filename) {
        if (filename == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lower.endsWith(".svg")) {
            return MediaType.parseMediaType("image/svg+xml");
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}