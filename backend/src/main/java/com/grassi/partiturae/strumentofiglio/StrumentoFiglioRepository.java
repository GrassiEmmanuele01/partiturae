package com.grassi.partiturae.strumentofiglio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrumentoFiglioRepository extends JpaRepository<StrumentoFiglio, Long> {
    List<StrumentoFiglio> findByStrumentoId(Long strumentoId);
}