package com.grassi.partiturae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Libretto;

@Repository
public interface LibrettoRepository extends JpaRepository<Libretto, Long> {
}