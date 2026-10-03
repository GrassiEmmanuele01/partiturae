package com.grassi.partiturae.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.LibrettoPartitura;

@Repository
public interface LibrettoPartituraRepository extends JpaRepository<LibrettoPartitura, Long> {
    List<LibrettoPartitura> findByLibrettoIdOrderByOrdineAsc(Long librettoId);
    Optional<LibrettoPartitura> findByLibrettoIdAndPartituraId(Long librettoId, Long partituraId);
    int countByLibrettoId(Long librettoId);
}