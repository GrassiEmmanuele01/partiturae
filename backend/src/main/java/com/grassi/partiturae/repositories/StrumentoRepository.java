package com.grassi.partiturae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Strumento;

@Repository
public interface StrumentoRepository extends JpaRepository<Strumento, Long> {
    List<Strumento> findByFamigliaId(Long famigliaId);
}