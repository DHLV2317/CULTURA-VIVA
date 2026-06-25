package com.santaculturaviva.repository;

import com.santaculturaviva.model.Usuario;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository
    extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByCorreoIgnoreCase(
        String correo
    );

    boolean existsByCorreoIgnoreCase(
        String correo
    );

    boolean existsByCorreoIgnoreCaseAndIdNot(
        String correo,
        Long id
    );

    @EntityGraph(attributePaths = "roles")
    List<Usuario> findAllByOrderByFechaCreacionDesc();

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findById(Long id);
}