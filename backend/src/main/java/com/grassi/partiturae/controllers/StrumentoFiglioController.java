package com.grassi.partiturae.controllers;

import com.grassi.partiturae.dto.StrumentoFiglioRequest;
import com.grassi.partiturae.dto.StrumentoFiglioResponse;
import com.grassi.partiturae.services.StrumentoFiglioService;
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
@RequestMapping("/api/strumenti-figli")
public class StrumentoFiglioController {

    private final StrumentoFiglioService strumentoFiglioService;

    public StrumentoFiglioController(StrumentoFiglioService strumentoFiglioService) {
        this.strumentoFiglioService = strumentoFiglioService;
    }

    @GetMapping
    public ResponseEntity<List<StrumentoFiglioResponse>> getAll(@RequestParam(required = false) Long strumentoId) {
        if (strumentoId != null) {
            return ResponseEntity.ok(strumentoFiglioService.getByStrumento(strumentoId));
        }
        return ResponseEntity.ok(strumentoFiglioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrumentoFiglioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(strumentoFiglioService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StrumentoFiglioResponse> create(@Valid @RequestBody StrumentoFiglioRequest request) {
        return ResponseEntity.status(201).body(strumentoFiglioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrumentoFiglioResponse> update(@PathVariable Long id, @Valid @RequestBody StrumentoFiglioRequest request) {
        return ResponseEntity.ok(strumentoFiglioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        strumentoFiglioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}