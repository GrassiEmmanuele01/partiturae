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

import com.grassi.partiturae.dto.PartituraRequest;
import com.grassi.partiturae.dto.PartituraResponse;
import com.grassi.partiturae.services.PartituraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/partiture")
public class PartituraController {

    private final PartituraService partituraService;

    public PartituraController(PartituraService partituraService) {
        this.partituraService = partituraService;
    }

    @GetMapping
    public ResponseEntity<List<PartituraResponse>> getAll() {
        return ResponseEntity.ok(partituraService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartituraResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(partituraService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PartituraResponse> create(@Valid @RequestBody PartituraRequest request) {
        return ResponseEntity.status(201).body(partituraService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartituraResponse> update(@PathVariable Long id, @Valid @RequestBody PartituraRequest request) {
        return ResponseEntity.ok(partituraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partituraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}