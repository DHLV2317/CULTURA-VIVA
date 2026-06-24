// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/service/
// AlertaService.java
// ==========================================================

package com.santaculturaviva.service;

import com.santaculturaviva.model.Alerta;
import com.santaculturaviva.repository.AlertaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlertaService {

    private static final String ESTADO_PUBLICADA =
        "PUBLICADA";

    private final AlertaRepository repository;

    public AlertaService(
        AlertaRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Alerta> listarPublicadas() {
        return repository
            .findByEstadoOrderByDestacadaDescFechaPublicacionDesc(
                ESTADO_PUBLICADA
            );
    }

    @Transactional(readOnly = true)
    public Alerta buscarPublicadaPorSlug(
        String slug
    ) {
        return repository
            .findBySlugAndEstado(
                slug,
                ESTADO_PUBLICADA
            )
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La alerta solicitada no existe o no está publicada."
                )
            );
    }
}