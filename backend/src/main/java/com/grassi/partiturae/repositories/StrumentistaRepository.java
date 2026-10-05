package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Strumentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrumentistaRepository extends JpaRepository<Strumentista, Long> {
}