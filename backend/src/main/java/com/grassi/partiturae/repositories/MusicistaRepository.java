package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Musicista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicistaRepository extends JpaRepository<Musicista, Long> {
}