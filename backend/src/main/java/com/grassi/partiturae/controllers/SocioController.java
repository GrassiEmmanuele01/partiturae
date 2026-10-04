package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.SocioRequest;
import com.grassi.partiturae.dto.SocioResponse;
import com.grassi.partiturae.services.SocioService;
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
@RequestMapping("/api/soci")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @GetMapping
    public ResponseEntity<List<SocioResponse>> getAll() {
        return ResponseEntity.ok(socioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SocioResponse> create(@Valid @RequestBody SocioRequest request) {
        return ResponseEntity.status(201).body(socioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocioResponse> update(@PathVariable Long id, @Valid @RequestBody SocioRequest request) {
        return ResponseEntity.ok(socioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        socioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}