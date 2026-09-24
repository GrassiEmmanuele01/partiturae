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

import com.grassi.partiturae.dto.StrumentoRequest;
import com.grassi.partiturae.dto.StrumentoResponse;
import com.grassi.partiturae.services.StrumentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/strumenti")
public class StrumentoController {

    private final StrumentoService strumentoService;

    public StrumentoController(StrumentoService strumentoService) {
        this.strumentoService = strumentoService;
    }

    @GetMapping
    public ResponseEntity<List<StrumentoResponse>> getAll(@RequestParam(required = false) Long famigliaId) {
        if (famigliaId != null) {
            return ResponseEntity.ok(strumentoService.getByFamiglia(famigliaId));
        }
        return ResponseEntity.ok(strumentoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrumentoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(strumentoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StrumentoResponse> create(@Valid @RequestBody StrumentoRequest request) {
        return ResponseEntity.status(201).body(strumentoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrumentoResponse> update(@PathVariable Long id, @Valid @RequestBody StrumentoRequest request) {
        return ResponseEntity.ok(strumentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        strumentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}