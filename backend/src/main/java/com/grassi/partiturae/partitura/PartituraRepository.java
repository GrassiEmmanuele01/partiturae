package com.grassi.partiturae.partitura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartituraRepository extends JpaRepository<Partitura, Long> {
    List<Partitura> findByAutoreId(Long autoreId);
    List<Partitura> findByNomeContainingIgnoreCase(String nome);
}