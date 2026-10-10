package com.grassi.partiturae.musicista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicistaRepository extends JpaRepository<Musicista, Long> {
    List<Musicista> findByStrumentiId(Long strumentoId);
    Optional<Musicista> findBySocioId(Long socioId);
}