// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/model/Alerta.java
// ==========================================================

package com.santaculturaviva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "alertas",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_alertas_slug",
            columnNames = "slug"
        )
    }
)
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        length = 180
    )
    private String titulo;

    @Column(
        nullable = false,
        length = 200
    )
    private String slug;

    @Column(
        nullable = false,
        length = 600
    )
    private String resumen;

    @Column(
        nullable = false,
        columnDefinition = "LONGTEXT"
    )
    private String contenido;

    @Column(
        nullable = false,
        length = 80
    )
    private String categoria;

    @Column(length = 120)
    private String territorio;

    @Column(length = 180)
    private String fuente;

    @Column(
        name = "url_fuente",
        length = 500
    )
    private String urlFuente;

    @Column(length = 500)
    private String imagen;

    @Column(nullable = false)
    private boolean destacada;

    @Column(
        nullable = false,
        length = 20
    )
    private String estado;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Column(
        name = "fecha_creacion",
        nullable = false,
        updatable = false
    )
    private LocalDateTime fechaCreacion;

    @Column(
        name = "fecha_actualizacion",
        nullable = false
    )
    private LocalDateTime fechaActualizacion;

    public Alerta() {
    }

    @PrePersist
    public void antesDeGuardar() {
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }

        fechaActualizacion = ahora;

        if (estado == null || estado.isBlank()) {
            estado = "BORRADOR";
        }

        if (
            "PUBLICADA".equalsIgnoreCase(estado)
                && fechaPublicacion == null
        ) {
            fechaPublicacion = ahora;
        }
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();

        if (
            "PUBLICADA".equalsIgnoreCase(estado)
                && fechaPublicacion == null
        ) {
            fechaPublicacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
        LocalDateTime fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
        LocalDateTime fechaActualizacion
    ) {
        this.fechaActualizacion = fechaActualizacion;
    }
}