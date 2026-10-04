package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.MembroDirettivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembroDirettivoRepository extends JpaRepository<MembroDirettivo, Long> {
}