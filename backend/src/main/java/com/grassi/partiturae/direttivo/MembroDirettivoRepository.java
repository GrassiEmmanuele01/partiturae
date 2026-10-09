package com.grassi.partiturae.direttivo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembroDirettivoRepository extends JpaRepository<MembroDirettivo, Long> {
}