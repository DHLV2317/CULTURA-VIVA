// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/repository/
// MensajeContactoRepository.java
// ==========================================================

package com.santaculturaviva.repository;

import com.santaculturaviva.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeContactoRepository
    extends JpaRepository<MensajeContacto, Long> {
}