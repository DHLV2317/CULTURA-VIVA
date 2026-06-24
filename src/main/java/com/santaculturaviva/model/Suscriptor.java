package com.santaculturaviva.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
    name = "suscriptores",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_suscriptores_correo",
            columnNames = "correo"
        )
    }
)
public class Suscriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "nombre_social",
        nullable = false,
        length = 120
    )
    private String nombreSocial;

    @Column(length = 50)
    private String pronombre;

    @Column(
        nullable = false,
        length = 150
    )
    private String correo;

    @Column(
        name = "tipo_vinculo",
        nullable = false,
        length = 80
    )
    private String tipoVinculo;

    @ElementCollection
    @CollectionTable(
        name = "suscriptor_intereses",
        joinColumns = @JoinColumn(name = "suscriptor_id")
    )
    @Column(
        name = "interes",
        nullable = false,
        length = 50
    )
    private Set<String> intereses = new LinkedHashSet<>();

    @Column(
        nullable = false,
        length = 30
    )
    private String frecuencia;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private boolean consentimiento;

    @Column(nullable = false)
    private boolean activo;

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

    public Suscriptor() {
    }

    @PrePersist
    public void antesDeGuardar() {
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }

        fechaActualizacion = ahora;
        activo = true;
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreSocial() {
        return nombreSocial;
    }

    public void setNombreSocial(String nombreSocial) {
        this.nombreSocial = nombreSocial;
    }

    public String getPronombre() {
        return pronombre;
    }

    public void setPronombre(String pronombre) {
        this.pronombre = pronombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(String tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public Set<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(Set<String> intereses) {
        this.intereses = intereses;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isConsentimiento() {
        return consentimiento;
    }

    public void setConsentimiento(boolean consentimiento) {
        this.consentimiento = consentimiento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
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