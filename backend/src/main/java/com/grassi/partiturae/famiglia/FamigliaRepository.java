package com.grassi.partiturae.famiglia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamigliaRepository extends JpaRepository<Famiglia, Long> {
}