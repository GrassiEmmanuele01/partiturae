package com.grassi.partiturae.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grassi.partiturae.dto.TesseramentoRequest;
import com.grassi.partiturae.dto.TesseramentoResponse;
import com.grassi.partiturae.dto.TesseramentoSummaryResponse;
import com.grassi.partiturae.services.TesseramentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bandisti/{bandistaId}/tesseramenti")
public class TesseramentoController {

    private final TesseramentoService tesseramentoService;

    public TesseramentoController(TesseramentoService tesseramentoService) {
        this.tesseramentoService = tesseramentoService;
    }

    @GetMapping
    public ResponseEntity<List<TesseramentoResponse>> getAll(@PathVariable Long bandistaId) {
        return ResponseEntity.ok(tesseramentoService.getByBandista(bandistaId));
    }

    @GetMapping("/summary")
    public ResponseEntity<TesseramentoSummaryResponse> getSummary(@PathVariable Long bandistaId) {
        return ResponseEntity.ok(tesseramentoService.getSummary(bandistaId));
    }

    @PutMapping("/{anno}")
    public ResponseEntity<TesseramentoResponse> upsert(
            @PathVariable Long bandistaId,
            @PathVariable Integer anno,
            @Valid @RequestBody TesseramentoRequest request) {
        return ResponseEntity.ok(tesseramentoService.upsert(bandistaId, anno, request));
    }

    @DeleteMapping("/{anno}")
    public ResponseEntity<Void> delete(@PathVariable Long bandistaId, @PathVariable Integer anno) {
        tesseramentoService.delete(bandistaId, anno);
        return ResponseEntity.noContent().build();
    }
}