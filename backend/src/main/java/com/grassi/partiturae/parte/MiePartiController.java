package com.grassi.partiturae.parte;

import com.grassi.partiturae.common.dto.FileDownloadResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mie-parti")
public class MiePartiController {

    private final MiePartiService miePartiService;

    public MiePartiController(MiePartiService miePartiService) {
        this.miePartiService = miePartiService;
    }

    @GetMapping
    public ResponseEntity<MiePartiResponse> mieParti(@RequestParam(required = false) Long partituraId) {
        return ResponseEntity.ok(miePartiService.mieParti(partituraId));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        FileDownloadResponse file = miePartiService.pdf(id);
        ContentDisposition disposition = ContentDisposition.inline().filename(file.filename()).build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.content());
    }
}