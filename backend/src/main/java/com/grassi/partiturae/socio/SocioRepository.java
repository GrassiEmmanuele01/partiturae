package com.grassi.partiturae.socio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {
    Optional<Socio> findByCodiceFiscale(String codiceFiscale);
    Optional<Socio> findByMail(String mail);
    Optional<Socio> findByMailIgnoreCase(String mail);
}