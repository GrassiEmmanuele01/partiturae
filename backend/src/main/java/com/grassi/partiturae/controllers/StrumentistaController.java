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

import com.grassi.partiturae.dto.StrumentistaRequest;
import com.grassi.partiturae.dto.StrumentistaResponse;
import com.grassi.partiturae.dto.StrumentistaStrumentiRequest;
import com.grassi.partiturae.services.StrumentistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/strumentisti")
public class StrumentistaController {

    private final StrumentistaService strumentistaService;

    public StrumentistaController(StrumentistaService strumentistaService) {
        this.strumentistaService = strumentistaService;
    }

    @GetMapping
    public ResponseEntity<List<StrumentistaResponse>> getAll() {
        return ResponseEntity.ok(strumentistaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrumentistaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(strumentistaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StrumentistaResponse> create(@Valid @RequestBody StrumentistaRequest request) {
        StrumentistaResponse response = strumentistaService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrumentistaResponse> update(@PathVariable Long id, @Valid @RequestBody StrumentistaRequest request) {
        return ResponseEntity.ok(strumentistaService.update(id, request));
    }

    @PutMapping("/{id}/strumenti")
    public ResponseEntity<StrumentistaResponse> updateStrumenti(@PathVariable Long id, @Valid @RequestBody StrumentistaStrumentiRequest request) {
        return ResponseEntity.ok(strumentistaService.updateStrumenti(id, request.getStrumentoIds()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        strumentistaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}