package com.grassi.partiturae.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grassi.partiturae.model.Bandista;

@Repository
public interface BandistaRepository extends JpaRepository<Bandista, Long> {
    Optional<Bandista> findByCodiceFiscale(String codiceFiscale);
    Optional<Bandista> findByMail(String mail);
}