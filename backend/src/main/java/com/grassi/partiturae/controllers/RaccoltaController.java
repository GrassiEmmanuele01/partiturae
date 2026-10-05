package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.AddPartituraRequest;
import com.grassi.partiturae.dto.RaccoltaRequest;
import com.grassi.partiturae.dto.RaccoltaResponse;
import com.grassi.partiturae.dto.ReorderPartitureRequest;
import com.grassi.partiturae.services.RaccoltaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/raccolte")
public class RaccoltaController {

    private final RaccoltaService raccoltaService;

    public RaccoltaController(RaccoltaService raccoltaService) {
        this.raccoltaService = raccoltaService;
    }

    @GetMapping
    public ResponseEntity<List<RaccoltaResponse>> getAll() {
        return ResponseEntity.ok(raccoltaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaccoltaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(raccoltaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RaccoltaResponse> create(@Valid @RequestBody RaccoltaRequest request) {
        return ResponseEntity.status(201).body(raccoltaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaccoltaResponse> update(@PathVariable Long id, @Valid @RequestBody RaccoltaRequest request) {
        return ResponseEntity.ok(raccoltaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        raccoltaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/partiture")
    public ResponseEntity<RaccoltaResponse> addPartitura(@PathVariable Long id, @Valid @RequestBody AddPartituraRequest request) {
        return ResponseEntity.ok(raccoltaService.addPartitura(id, request.getPartituraId()));
    }

    @DeleteMapping("/{id}/partiture/{partituraId}")
    public ResponseEntity<RaccoltaResponse> removePartitura(@PathVariable Long id, @PathVariable Long partituraId) {
        return ResponseEntity.ok(raccoltaService.removePartitura(id, partituraId));
    }

    @PutMapping("/{id}/partiture/ordine")
    public ResponseEntity<RaccoltaResponse> reorder(@PathVariable Long id, @Valid @RequestBody ReorderPartitureRequest request) {
        return ResponseEntity.ok(raccoltaService.reorder(id, request.getPartituraIdsInOrdine()));
    }
}