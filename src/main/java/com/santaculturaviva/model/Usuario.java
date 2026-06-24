package com.santaculturaviva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
name = "usuarios",
uniqueConstraints = {
@UniqueConstraint(
name = "uk_usuarios_correo",
columnNames = "correo"
)
}
)
public class Usuario {

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
    name = "password_hash",
    nullable = false,
    length = 255
)
private String passwordHash;

@Column(nullable = false)
private boolean activo = true;

@Column(
    name = "correo_verificado",
    nullable = false
)
private boolean correoVerificado = false;

@Column(nullable = false)
private boolean bloqueado = false;

@Column(
    name = "intentos_fallidos",
    nullable = false
)
private int intentosFallidos = 0;

@Column(name = "ultimo_acceso")
private LocalDateTime ultimoAcceso;

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

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "usuarios_roles",
    joinColumns = {
        @JoinColumn(
            name = "usuario_id"
        )
    },
    inverseJoinColumns = {
        @JoinColumn(
            name = "rol_id"
        )
    }
)
private Set<Rol> roles = new LinkedHashSet<>();

public Usuario() {
}

@PrePersist
public void antesDeGuardar() {
    LocalDateTime ahora = LocalDateTime.now();

    if (fechaCreacion == null) {
        fechaCreacion = ahora;
    }

    fechaActualizacion = ahora;
}

@PreUpdate
public void antesDeActualizar() {
    fechaActualizacion = LocalDateTime.now();
}

public void agregarRol(Rol rol) {
    if (rol != null) {
        roles.add(rol);
    }
}

public void quitarRol(Rol rol) {
    roles.remove(rol);
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

public void setNombreSocial(
    String nombreSocial
) {
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

public String getPasswordHash() {
    return passwordHash;
}

public void setPasswordHash(
    String passwordHash
) {
    this.passwordHash = passwordHash;
}

public boolean isActivo() {
    return activo;
}

public void setActivo(boolean activo) {
    this.activo = activo;
}

public boolean isCorreoVerificado() {
    return correoVerificado;
}

public void setCorreoVerificado(
    boolean correoVerificado
) {
    this.correoVerificado = correoVerificado;
}

public boolean isBloqueado() {
    return bloqueado;
}

public void setBloqueado(
    boolean bloqueado
) {
    this.bloqueado = bloqueado;
}

public int getIntentosFallidos() {
    return intentosFallidos;
}

public void setIntentosFallidos(
    int intentosFallidos
) {
    this.intentosFallidos = intentosFallidos;
}

public LocalDateTime getUltimoAcceso() {
    return ultimoAcceso;
}

public void setUltimoAcceso(
    LocalDateTime ultimoAcceso
) {
    this.ultimoAcceso = ultimoAcceso;
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

public Set<Rol> getRoles() {
    return roles;
}

public void setRoles(Set<Rol> roles) {
    this.roles = roles == null
        ? new LinkedHashSet<>()
        : roles;
}

}
