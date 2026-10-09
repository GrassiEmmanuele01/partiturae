package com.grassi.partiturae.direttivo;

import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/direttivo")
public class MembroDirettivoController {

    private final MembroDirettivoService membroDirettivoService;

    public MembroDirettivoController(MembroDirettivoService membroDirettivoService) {
        this.membroDirettivoService = membroDirettivoService;
    }

    @GetMapping
    public ResponseEntity<List<MembroDirettivoResponse>> getAll(@RequestParam(required = false) Boolean inCarica) {
        if (Boolean.TRUE.equals(inCarica)) {
            return ResponseEntity.ok(membroDirettivoService.getInCarica());
        }
        return ResponseEntity.ok(membroDirettivoService.getAll());
    }

    @PostMapping
    public ResponseEntity<MembroDirettivoResponse> create(@Valid @RequestBody MembroDirettivoRequest request) {
        return ResponseEntity.status(201).body(membroDirettivoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembroDirettivoResponse> update(@PathVariable Long id, @Valid @RequestBody MembroDirettivoRequest request) {
        return ResponseEntity.ok(membroDirettivoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membroDirettivoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}