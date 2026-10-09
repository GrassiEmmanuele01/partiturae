package com.grassi.partiturae.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresenzaRepository extends JpaRepository<Presenza, Long> {
    List<Presenza> findByEventoId(Long eventoId);
    Optional<Presenza> findByEventoIdAndSocioId(Long eventoId, Long socioId);
    int countByEventoIdAndPresenteTrue(Long eventoId);
}