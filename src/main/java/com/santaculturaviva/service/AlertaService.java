package com.santaculturaviva.service;

import com.santaculturaviva.dto.AlertaFormulario;
import com.santaculturaviva.model.Alerta;
import com.santaculturaviva.repository.AlertaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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

    // ======================================================
    // SITIO PÚBLICO
    // ======================================================

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
                    "La alerta no existe o no está publicada."
                )
            );
    }

    // ======================================================
    // PANEL ADMINISTRATIVO
    // ======================================================

    @Transactional(readOnly = true)
    public List<Alerta> listarTodas() {
        return repository
            .findAllByOrderByFechaActualizacionDesc();
    }

    @Transactional(readOnly = true)
    public Alerta buscarPorId(Long id) {
        return repository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La alerta solicitada no existe."
                )
            );
    }

    @Transactional(readOnly = true)
    public AlertaFormulario obtenerFormulario(
        Long id
    ) {
        Alerta alerta = buscarPorId(id);

        AlertaFormulario formulario =
            new AlertaFormulario();

        formulario.setTitulo(
            alerta.getTitulo()
        );

        formulario.setSlug(
            alerta.getSlug()
        );

        formulario.setResumen(
            alerta.getResumen()
        );

        formulario.setContenido(
            alerta.getContenido()
        );

        formulario.setCategoria(
            alerta.getCategoria()
        );

        formulario.setTerritorio(
            alerta.getTerritorio()
        );

        formulario.setFuente(
            alerta.getFuente()
        );

        formulario.setUrlFuente(
            alerta.getUrlFuente()
        );

        formulario.setImagen(
            alerta.getImagen()
        );

        formulario.setDestacada(
            alerta.isDestacada()
        );

        formulario.setEstado(
            alerta.getEstado()
        );

        formulario.setFechaPublicacion(
            alerta.getFechaPublicacion()
        );

        return formulario;
    }

    @Transactional
    public Alerta crear(
        AlertaFormulario formulario
    ) {
        Alerta alerta = new Alerta();

        copiarFormulario(
            formulario,
            alerta
        );

        alerta.setSlug(
            generarSlugUnico(
                formulario.getSlug(),
                formulario.getTitulo(),
                null
            )
        );

        if (alerta.isDestacada()) {
            desmarcarOtrasDestacadas(null);
        }

        return repository.save(alerta);
    }

    @Transactional
    public Alerta actualizar(
        Long id,
        AlertaFormulario formulario
    ) {
        Alerta alerta = buscarPorId(id);

        copiarFormulario(
            formulario,
            alerta
        );

        alerta.setSlug(
            generarSlugUnico(
                formulario.getSlug(),
                formulario.getTitulo(),
                id
            )
        );

        if (alerta.isDestacada()) {
            desmarcarOtrasDestacadas(id);
        }

        return repository.save(alerta);
    }

    @Transactional
    public void eliminar(Long id) {
        Alerta alerta = buscarPorId(id);

        repository.delete(alerta);
    }

    private void copiarFormulario(
        AlertaFormulario formulario,
        Alerta alerta
    ) {
        alerta.setTitulo(
            limpiar(formulario.getTitulo())
        );

        alerta.setResumen(
            limpiar(formulario.getResumen())
        );

        alerta.setContenido(
            limpiar(formulario.getContenido())
        );

        alerta.setCategoria(
            limpiar(formulario.getCategoria())
        );

        alerta.setTerritorio(
            limpiarOpcional(
                formulario.getTerritorio()
            )
        );

        alerta.setFuente(
            limpiarOpcional(
                formulario.getFuente()
            )
        );

        alerta.setUrlFuente(
            limpiarOpcional(
                formulario.getUrlFuente()
            )
        );

        alerta.setImagen(
            limpiarOpcional(
                formulario.getImagen()
            )
        );

        alerta.setEstado(
            limpiar(formulario.getEstado())
                .toUpperCase(Locale.ROOT)
        );

        alerta.setDestacada(
            formulario.isDestacada()
        );

        alerta.setFechaPublicacion(
            formulario.getFechaPublicacion()
        );

        if (
            ESTADO_PUBLICADA.equals(
                alerta.getEstado()
            )
            && alerta.getFechaPublicacion() == null
        ) {
            alerta.setFechaPublicacion(
                LocalDateTime.now()
            );
        }

        if (
            !ESTADO_PUBLICADA.equals(
                alerta.getEstado()
            )
        ) {
            alerta.setDestacada(false);
        }
    }

    private void desmarcarOtrasDestacadas(
        Long idActual
    ) {
        List<Alerta> destacadas =
            repository.findByDestacadaTrue();

        for (Alerta alerta : destacadas) {
            boolean esLaAlertaActual =
                idActual != null
                    && alerta.getId().equals(idActual);

            if (!esLaAlertaActual) {
                alerta.setDestacada(false);
            }
        }

        repository.saveAll(destacadas);
    }

    private String generarSlugUnico(
        String slugIngresado,
        String titulo,
        Long idActual
    ) {
        String textoBase;

        if (
            slugIngresado == null
                || slugIngresado.isBlank()
        ) {
            textoBase = titulo;
        } else {
            textoBase = slugIngresado;
        }

        String base =
            convertirASlug(textoBase);

        if (base.isBlank()) {
            base = "alerta";
        }

        String candidato = base;
        int numero = 2;

        while (
            slugExiste(
                candidato,
                idActual
            )
        ) {
            candidato =
                base + "-" + numero;

            numero++;
        }

        return candidato;
    }

    private boolean slugExiste(
        String slug,
        Long idActual
    ) {
        if (idActual == null) {
            return repository.existsBySlug(slug);
        }

        return repository.existsBySlugAndIdNot(
            slug,
            idActual
        );
    }

    private String convertirASlug(
        String texto
    ) {
        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer
            .normalize(
                texto,
                Normalizer.Form.NFD
            )
            .replaceAll("\\p{M}", "");

        return sinTildes
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    }

    private String limpiar(String valor) {
        return valor == null
            ? ""
            : valor.trim();
    }

    private String limpiarOpcional(
        String valor
    ) {
        if (
            valor == null
                || valor.isBlank()
        ) {
            return null;
        }

        return valor.trim();
    }
}