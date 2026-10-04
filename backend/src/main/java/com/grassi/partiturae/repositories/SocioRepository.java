package com.grassi.partiturae.repositories;

import com.grassi.partiturae.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {
    Optional<Socio> findByCodiceFiscale(String codiceFiscale);
    Optional<Socio> findByMail(String mail);
}