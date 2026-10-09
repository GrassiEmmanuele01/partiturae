package com.grassi.partiturae.autore;

import com.grassi.partiturae.common.dto.UtilizzoResponse;
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
@RequestMapping("/api/autori")
public class AutoreController {

    private final AutoreService autoreService;

    public AutoreController(AutoreService autoreService) {
        this.autoreService = autoreService;
    }

    @GetMapping
    public ResponseEntity<List<AutoreResponse>> getAll() {
        return ResponseEntity.ok(autoreService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutoreResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(autoreService.getById(id));
    }

    @GetMapping("/{id}/utilizzo")
    public ResponseEntity<UtilizzoResponse> getUtilizzo(@PathVariable Long id) {
        return ResponseEntity.ok(autoreService.getUtilizzo(id));
    }

    @PostMapping
    public ResponseEntity<AutoreResponse> create(@Valid @RequestBody AutoreRequest request) {
        return ResponseEntity.status(201).body(autoreService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutoreResponse> update(@PathVariable Long id, @Valid @RequestBody AutoreRequest request) {
        return ResponseEntity.ok(autoreService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autoreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}