package com.grassi.partiturae.formazione;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormazioneRepository extends JpaRepository<Formazione, Long> {
}