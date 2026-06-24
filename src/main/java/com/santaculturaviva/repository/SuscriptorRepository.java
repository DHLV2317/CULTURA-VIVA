package com.santaculturaviva.repository;

import com.santaculturaviva.model.Suscriptor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscriptorRepository
    extends JpaRepository<Suscriptor, Long> {

    boolean existsByCorreoIgnoreCase(String correo);
}