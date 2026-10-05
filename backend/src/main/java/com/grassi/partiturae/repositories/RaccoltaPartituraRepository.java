package com.grassi.partiturae.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.RaccoltaPartitura;

@Repository
public interface RaccoltaPartituraRepository extends JpaRepository<RaccoltaPartitura, Long> {
    List<RaccoltaPartitura> findByRaccoltaIdOrderByOrdineAsc(Long raccoltaId);
    Optional<RaccoltaPartitura> findByRaccoltaIdAndPartituraId(Long raccoltaId, Long partituraId);
    int countByRaccoltaId(Long raccoltaId);
}