package com.grassi.partiturae.strumento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrumentoRepository extends JpaRepository<Strumento, Long> {
    List<Strumento> findByFamigliaId(Long famigliaId);
}