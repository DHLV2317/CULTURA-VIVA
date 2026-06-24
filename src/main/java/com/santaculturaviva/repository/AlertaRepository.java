package com.santaculturaviva.repository;

import com.santaculturaviva.model.Alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaRepository
    extends JpaRepository<Alerta, Long> {

    /*
     * Obtiene las alertas públicas.
     * Primero muestra la destacada y después ordena
     * por fecha de publicación.
     */
    List<Alerta>
    findByEstadoOrderByDestacadaDescFechaPublicacionDesc(
        String estado
    );

    /*
     * Busca una alerta pública mediante su slug.
     */
    Optional<Alerta> findBySlugAndEstado(
        String slug,
        String estado
    );

    /*
     * Lista todas las alertas para el panel administrativo,
     * comenzando por la modificada más recientemente.
     */
    List<Alerta>
    findAllByOrderByFechaActualizacionDesc();

    /*
     * Busca las alertas actualmente marcadas
     * como destacadas.
     */
    List<Alerta> findByDestacadaTrue();

    /*
     * Comprueba si un slug ya existe al crear una alerta.
     */
    boolean existsBySlug(String slug);

    /*
     * Comprueba si un slug pertenece a otra alerta
     * cuando se está editando.
     */
    boolean existsBySlugAndIdNot(
        String slug,
        Long id
    );
}