package com.grassi.partiturae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Famiglia;

@Repository
public interface FamigliaRepository extends JpaRepository<Famiglia, Long> {
}