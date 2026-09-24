package com.grassi.partiturae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Parte;

@Repository
public interface ParteRepository extends JpaRepository<Parte, Long> {
    List<Parte> findByPartituraId(Long partituraId);
    List<Parte> findByStrumentoFiglioId(Long strumentoFiglioId);
}