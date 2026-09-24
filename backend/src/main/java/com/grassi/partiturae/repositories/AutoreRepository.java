package com.grassi.partiturae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Autore;

@Repository
public interface AutoreRepository extends JpaRepository<Autore, Long> {
    List<Autore> findByNominativoContainingIgnoreCase(String nominativo);
}