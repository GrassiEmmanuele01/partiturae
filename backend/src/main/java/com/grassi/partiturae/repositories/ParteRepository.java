package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Parte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParteRepository extends JpaRepository<Parte, Long> {
    List<Parte> findByPartituraId(Long partituraId);
    List<Parte> findByStrumentoFiglioId(Long strumentoFiglioId);
    List<Parte> findByStrumentoFiglio_StrumentoId(Long strumentoId);
}