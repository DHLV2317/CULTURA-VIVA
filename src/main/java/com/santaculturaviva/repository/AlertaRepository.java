// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/repository/
// AlertaRepository.java
// ==========================================================

package com.santaculturaviva.repository;

import com.santaculturaviva.model.Alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaRepository
    extends JpaRepository<Alerta, Long> {

    List<Alerta>
    findByEstadoOrderByDestacadaDescFechaPublicacionDesc(
        String estado
    );

    Optional<Alerta> findBySlugAndEstado(
        String slug,
        String estado
    );

    boolean existsBySlug(String slug);
}