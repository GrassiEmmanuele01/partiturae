package com.grassi.partiturae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Partitura;

@Repository
public interface PartituraRepository extends JpaRepository<Partitura, Long> {
    List<Partitura> findByAutoreId(Long autoreId);
    List<Partitura> findByNomeContainingIgnoreCase(String nome);
}