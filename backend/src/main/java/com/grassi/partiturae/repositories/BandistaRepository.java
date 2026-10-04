package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Bandista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandistaRepository extends JpaRepository<Bandista, Long> {
}