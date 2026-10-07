package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.EventoRequest;
import com.grassi.partiturae.dto.EventoResponse;
import com.grassi.partiturae.services.EventoService;
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
@RequestMapping("/api/eventi")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> getAll() {
        return ResponseEntity.ok(eventoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EventoResponse> create(@Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(201).body(eventoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> update(@PathVariable Long id, @Valid @RequestBody EventoRequest request) {
        return ResponseEntity.ok(eventoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}