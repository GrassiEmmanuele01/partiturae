package com.grassi.partiturae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Raccolta;

@Repository
public interface RaccoltaRepository extends JpaRepository<Raccolta, Long> {
}