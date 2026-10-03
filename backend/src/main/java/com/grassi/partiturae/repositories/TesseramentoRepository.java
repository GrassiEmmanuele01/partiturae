package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Tesseramento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TesseramentoRepository extends JpaRepository<Tesseramento, Long> {
    List<Tesseramento> findByBandistaIdOrderByAnnoDesc(Long bandistaId);
    Optional<Tesseramento> findByBandistaIdAndAnno(Long bandistaId, Integer anno);
}