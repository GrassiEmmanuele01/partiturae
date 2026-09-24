package com.grassi.partiturae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Banda;

@Repository
public interface BandaRepository extends JpaRepository<Banda, Long> {
}