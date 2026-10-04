package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.IscrizioneSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IscrizioneSocioRepository extends JpaRepository<IscrizioneSocio, Long> {
    List<IscrizioneSocio> findBySocioIdOrderByAnnoDesc(Long socioId);
    Optional<IscrizioneSocio> findBySocioIdAndAnno(Long socioId, Integer anno);
    int countByAnnoAndIscrittoTrue(Integer anno);
}