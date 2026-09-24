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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grassi.partiturae.dto.ParteRequest;
import com.grassi.partiturae.dto.ParteResponse;
import com.grassi.partiturae.services.ParteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/parti")
public class ParteController {

    private final ParteService parteService;

    public ParteController(ParteService parteService) {
        this.parteService = parteService;
    }

    @GetMapping
    public ResponseEntity<List<ParteResponse>> getAll(@RequestParam(required = false) Long partituraId) {
        if (partituraId != null) {
            return ResponseEntity.ok(parteService.getByPartitura(partituraId));
        }
        return ResponseEntity.ok(parteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ParteResponse> create(@Valid @RequestBody ParteRequest request) {
        return ResponseEntity.status(201).body(parteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParteResponse> update(@PathVariable Long id, @Valid @RequestBody ParteRequest request) {
        return ResponseEntity.ok(parteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}