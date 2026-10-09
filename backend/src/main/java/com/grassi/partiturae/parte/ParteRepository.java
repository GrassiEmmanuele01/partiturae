package com.grassi.partiturae.parte;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParteRepository extends JpaRepository<Parte, Long> {

    @EntityGraph(attributePaths = {"partitura", "strumenti", "strumenti.strumento"})
    @Query("select distinct p from Parte p where p.partitura.id = :partituraId")
    List<Parte> findAllByPartitura(@Param("partituraId") Long partituraId);

    @EntityGraph(attributePaths = {"partitura", "strumenti", "strumenti.strumento"})
    @Query("select distinct p from Parte p where p.id in "
            + "(select p2.id from Parte p2 join p2.strumenti sf where sf.id = :strumentoFiglioId)")
    List<Parte> findAllByStrumentoFiglio(@Param("strumentoFiglioId") Long strumentoFiglioId);

    @EntityGraph(attributePaths = {"partitura", "strumenti", "strumenti.strumento"})
    @Query("select distinct p from Parte p where p.id in "
            + "(select p2.id from Parte p2 join p2.strumenti sf where sf.strumento.id = :strumentoId)")
    List<Parte> findAllByStrumento(@Param("strumentoId") Long strumentoId);
}
