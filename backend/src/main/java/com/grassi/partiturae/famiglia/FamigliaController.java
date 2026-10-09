package com.grassi.partiturae.famiglia;

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
@RequestMapping("/api/famiglie")
public class FamigliaController {

    private final FamigliaService famigliaService;

    public FamigliaController(FamigliaService famigliaService) {
        this.famigliaService = famigliaService;
    }

    @GetMapping
    public ResponseEntity<List<FamigliaResponse>> getAll() {
        return ResponseEntity.ok(famigliaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamigliaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(famigliaService.getById(id));
    }

    @GetMapping("/{id}/utilizzo")
    public ResponseEntity<UtilizzoResponse> getUtilizzo(@PathVariable Long id) {
        return ResponseEntity.ok(famigliaService.getUtilizzo(id));
    }

    @PostMapping
    public ResponseEntity<FamigliaResponse> create(@Valid @RequestBody FamigliaRequest request) {
        return ResponseEntity.status(201).body(famigliaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamigliaResponse> update(@PathVariable Long id, @Valid @RequestBody FamigliaRequest request) {
        return ResponseEntity.ok(famigliaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        famigliaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}