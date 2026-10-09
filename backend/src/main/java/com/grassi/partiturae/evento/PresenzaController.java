package com.grassi.partiturae.evento;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventi/{eventoId}/presenze")
public class PresenzaController {

    private final PresenzaService presenzaService;

    public PresenzaController(PresenzaService presenzaService) {
        this.presenzaService = presenzaService;
    }

    @GetMapping
    public ResponseEntity<List<PresenzaResponse>> getByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(presenzaService.getByEvento(eventoId));
    }

    @PutMapping("/{socioId}")
    public ResponseEntity<PresenzaResponse> upsert(
            @PathVariable Long eventoId,
            @PathVariable Long socioId,
            @Valid @RequestBody PresenzaRequest request) {
        return ResponseEntity.ok(presenzaService.upsert(eventoId, socioId, request));
    }
}
