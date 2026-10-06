package com.grassi.partiturae.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grassi.partiturae.dto.MusicistaRequest;
import com.grassi.partiturae.dto.MusicistaResponse;
import com.grassi.partiturae.dto.MusicistaStrumentiRequest;
import com.grassi.partiturae.services.MusicistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/musicisti")
public class MusicistaController {

    private final MusicistaService musicistaService;

    public MusicistaController(MusicistaService musicistaService) {
        this.musicistaService = musicistaService;
    }

    @GetMapping
    public ResponseEntity<List<MusicistaResponse>> getAll() {
        return ResponseEntity.ok(musicistaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicistaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(musicistaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MusicistaResponse> create(@Valid @RequestBody MusicistaRequest request) {
        MusicistaResponse response = musicistaService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicistaResponse> update(@PathVariable Long id, @Valid @RequestBody MusicistaRequest request) {
        return ResponseEntity.ok(musicistaService.update(id, request));
    }

    @PutMapping("/{id}/strumenti")
    public ResponseEntity<MusicistaResponse> updateStrumenti(@PathVariable Long id, @Valid @RequestBody MusicistaStrumentiRequest request) {
        return ResponseEntity.ok(musicistaService.updateStrumenti(id, request.getStrumentoIds()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        musicistaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}