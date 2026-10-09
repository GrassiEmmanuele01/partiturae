package com.grassi.partiturae.autore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoreRepository extends JpaRepository<Autore, Long> {
    List<Autore> findByNominativoContainingIgnoreCase(String nominativo);
}