package com.santaculturaviva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
name = "roles",
uniqueConstraints = {
@UniqueConstraint(
name = "uk_roles_nombre",
columnNames = "nombre"
)
}
)
public class Rol {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(
    nullable = false,
    length = 60
)
private String nombre;

@Column(
    nullable = false,
    length = 100
)
private String etiqueta;

@Column(length = 500)
private String descripcion;

@Column(nullable = false)
private boolean activo = true;

@Column(
    name = "fecha_creacion",
    nullable = false,
    updatable = false
)
private LocalDateTime fechaCreacion;

public Rol() {
}

@PrePersist
public void antesDeGuardar() {
    if (fechaCreacion == null) {
        fechaCreacion = LocalDateTime.now();
    }
}

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}

public String getEtiqueta() {
    return etiqueta;
}

public void setEtiqueta(String etiqueta) {
    this.etiqueta = etiqueta;
}

public String getDescripcion() {
    return descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
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

public void setFechaCreacion(
    LocalDateTime fechaCreacion
) {
    this.fechaCreacion = fechaCreacion;
}

}