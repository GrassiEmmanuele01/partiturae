package com.grassi.partiturae.raccolta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaccoltaRepository extends JpaRepository<Raccolta, Long> {
}