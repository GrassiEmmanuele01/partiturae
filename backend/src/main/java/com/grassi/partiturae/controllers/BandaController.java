package com.grassi.partiturae.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grassi.partiturae.dto.BandaRequest;
import com.grassi.partiturae.dto.BandaResponse;
import com.grassi.partiturae.services.BandaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/banda")
public class BandaController {

    private final BandaService bandaService;

    public BandaController(BandaService bandaService) {
        this.bandaService = bandaService;
    }

    @GetMapping
    public ResponseEntity<BandaResponse> get() {
        return ResponseEntity.ok(bandaService.getBanda());
    }

    @PutMapping
    public ResponseEntity<BandaResponse> save(@Valid @RequestBody BandaRequest request) {
        return ResponseEntity.ok(bandaService.createOrUpdate(request));
    }
}