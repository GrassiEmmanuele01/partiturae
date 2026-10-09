package com.grassi.partiturae.socio;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/soci/{socioId}/iscrizioni")
public class IscrizioneSocioController {

    private final IscrizioneSocioService iscrizioneSocioService;

    public IscrizioneSocioController(IscrizioneSocioService iscrizioneSocioService) {
        this.iscrizioneSocioService = iscrizioneSocioService;
    }

    @GetMapping
    public ResponseEntity<List<IscrizioneSocioResponse>> getAll(@PathVariable Long socioId) {
        return ResponseEntity.ok(iscrizioneSocioService.getBySocio(socioId));
    }

    @GetMapping("/summary")
    public ResponseEntity<IscrizioneSocioSummaryResponse> getSummary(@PathVariable Long socioId) {
        return ResponseEntity.ok(iscrizioneSocioService.getSummary(socioId));
    }

    @PutMapping("/{anno}")
    public ResponseEntity<IscrizioneSocioResponse> upsert(
            @PathVariable Long socioId,
            @PathVariable Integer anno,
            @Valid @RequestBody IscrizioneSocioRequest request) {
        return ResponseEntity.ok(iscrizioneSocioService.upsert(socioId, anno, request));
    }

    @DeleteMapping("/{anno}")
    public ResponseEntity<Void> delete(@PathVariable Long socioId, @PathVariable Integer anno) {
        iscrizioneSocioService.delete(socioId, anno);
        return ResponseEntity.noContent().build();
    }
}