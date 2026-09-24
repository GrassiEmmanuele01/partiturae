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

import com.grassi.partiturae.dto.BandistaRequest;
import com.grassi.partiturae.dto.BandistaResponse;
import com.grassi.partiturae.services.BandistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bandisti")
public class BandistaController {

    private final BandistaService bandistaService;

    public BandistaController(BandistaService bandistaService) {
        this.bandistaService = bandistaService;
    }

    @GetMapping
    public ResponseEntity<List<BandistaResponse>> getAll() {
        return ResponseEntity.ok(bandistaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BandistaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bandistaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BandistaResponse> create(@Valid @RequestBody BandistaRequest request) {
        BandistaResponse response = bandistaService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BandistaResponse> update(@PathVariable Long id, @Valid @RequestBody BandistaRequest request) {
        return ResponseEntity.ok(bandistaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bandistaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}