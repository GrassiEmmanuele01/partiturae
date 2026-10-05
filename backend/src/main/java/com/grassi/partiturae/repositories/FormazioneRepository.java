package com.grassi.partiturae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Formazione;

@Repository
public interface FormazioneRepository extends JpaRepository<Formazione, Long> {
}