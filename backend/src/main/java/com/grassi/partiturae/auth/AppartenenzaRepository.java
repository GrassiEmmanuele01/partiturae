package com.grassi.partiturae.auth;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppartenenzaRepository extends JpaRepository<Appartenenza, Long> {

    /** Le bande in cui l'account può entrare: appartenenza attiva e banda non bloccata. */
    @EntityGraph(attributePaths = "banda")
    @Query("select a from Appartenenza a where a.account.id = :accountId and a.attiva = true "
            + "and a.banda.attiva = true order by a.banda.nome")
    List<Appartenenza> findAttiveByAccount(@Param("accountId") Long accountId);
}
