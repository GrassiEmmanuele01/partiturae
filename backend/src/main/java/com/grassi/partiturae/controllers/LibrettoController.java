package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.AddPartituraRequest;
import com.grassi.partiturae.dto.LibrettoRequest;
import com.grassi.partiturae.dto.LibrettoResponse;
import com.grassi.partiturae.dto.ReorderPartitureRequest;
import com.grassi.partiturae.services.LibrettoService;
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
@RequestMapping("/api/libretti")
public class LibrettoController {

    private final LibrettoService librettoService;

    public LibrettoController(LibrettoService librettoService) {
        this.librettoService = librettoService;
    }

    @GetMapping
    public ResponseEntity<List<LibrettoResponse>> getAll() {
        return ResponseEntity.ok(librettoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibrettoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(librettoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<LibrettoResponse> create(@Valid @RequestBody LibrettoRequest request) {
        return ResponseEntity.status(201).body(librettoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibrettoResponse> update(@PathVariable Long id, @Valid @RequestBody LibrettoRequest request) {
        return ResponseEntity.ok(librettoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        librettoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/partiture")
    public ResponseEntity<LibrettoResponse> addPartitura(@PathVariable Long id, @Valid @RequestBody AddPartituraRequest request) {
        return ResponseEntity.ok(librettoService.addPartitura(id, request.getPartituraId()));
    }

    @DeleteMapping("/{id}/partiture/{partituraId}")
    public ResponseEntity<LibrettoResponse> removePartitura(@PathVariable Long id, @PathVariable Long partituraId) {
        return ResponseEntity.ok(librettoService.removePartitura(id, partituraId));
    }

    @PutMapping("/{id}/partiture/ordine")
    public ResponseEntity<LibrettoResponse> reorder(@PathVariable Long id, @Valid @RequestBody ReorderPartitureRequest request) {
        return ResponseEntity.ok(librettoService.reorder(id, request.getPartituraIdsInOrdine()));
    }
}