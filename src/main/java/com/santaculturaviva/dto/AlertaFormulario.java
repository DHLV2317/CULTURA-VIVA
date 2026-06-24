// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/dto/AlertaFormulario.java
// ==========================================================

package com.santaculturaviva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AlertaFormulario {

    @NotBlank(
        message = "El título es obligatorio."
    )
    @Size(
        max = 180,
        message = "El título no puede superar los 180 caracteres."
    )
    private String titulo;

    @Size(
        max = 200,
        message = "El slug no puede superar los 200 caracteres."
    )
    @Pattern(
        regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
        message = "El slug solo puede contener minúsculas, números y guiones."
    )
    private String slug;

    @NotBlank(
        message = "El resumen es obligatorio."
    )
    @Size(
        max = 600,
        message = "El resumen no puede superar los 600 caracteres."
    )
    private String resumen;

    @NotBlank(
        message = "El contenido es obligatorio."
    )
    @Size(
        min = 20,
        max = 50000,
        message = "El contenido debe tener entre 20 y 50000 caracteres."
    )
    private String contenido;

    @NotBlank(
        message = "La categoría es obligatoria."
    )
    @Size(
        max = 80,
        message = "La categoría no puede superar los 80 caracteres."
    )
    private String categoria;

    @Size(
        max = 120,
        message = "El territorio no puede superar los 120 caracteres."
    )
    private String territorio;

    @Size(
        max = 180,
        message = "La fuente no puede superar los 180 caracteres."
    )
    private String fuente;

    @Size(
        max = 500,
        message = "La URL no puede superar los 500 caracteres."
    )
    @Pattern(
        regexp = "^https?://.+$",
        message = "La URL debe comenzar con http:// o https://."
    )
    private String urlFuente;

    @Size(
        max = 500,
        message = "La ruta de imagen no puede superar los 500 caracteres."
    )
    private String imagen;

    private boolean destacada;

    @NotBlank(
        message = "Selecciona el estado de la alerta."
    )
    @Pattern(
        regexp = "BORRADOR|PUBLICADA",
        message = "El estado debe ser BORRADOR o PUBLICADA."
    )
    private String estado = "BORRADOR";

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    private LocalDateTime fechaPublicacion;

    public AlertaFormulario() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTerritorio() {
        return territorio;
    }

    public void setTerritorio(String territorio) {
        this.territorio = territorio;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getUrlFuente() {
        return urlFuente;
    }

    public void setUrlFuente(String urlFuente) {
        this.urlFuente = urlFuente;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isDestacada() {
        return destacada;
    }

    public void setDestacada(boolean destacada) {
        this.destacada = destacada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(
        LocalDateTime fechaPublicacion
    ) {
        this.fechaPublicacion = fechaPublicacion;
    }
}