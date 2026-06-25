package com.santaculturaviva.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

public class UsuarioFormulario {

    @NotBlank(
        message = "El nombre social es obligatorio."
    )
    @Size(
        min = 2,
        max = 120,
        message = "El nombre social debe tener entre 2 y 120 caracteres."
    )
    private String nombreSocial;

    @Size(
        max = 50,
        message = "El pronombre no puede superar los 50 caracteres."
    )
    private String pronombre;

    @NotBlank(
        message = "El correo es obligatorio."
    )
    @Email(
        message = "Ingresa un correo válido."
    )
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres."
    )
    private String correo;

    @Size(
        max = 100,
        message = "La contraseña no puede superar los 100 caracteres."
    )
    private String password;

    @Size(
        max = 100,
        message = "La confirmación no puede superar los 100 caracteres."
    )
    private String confirmarPassword;

    @NotEmpty(
        message = "Selecciona al menos un rol."
    )
    private Set<Long> rolesIds = new LinkedHashSet<>();

    private boolean activo = true;

    private boolean correoVerificado = true;

    private boolean bloqueado = false;

    public UsuarioFormulario() {
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

    public void setPronombre(
        String pronombre
    ) {
        this.pronombre = pronombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(
        String correo
    ) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
        String password
    ) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(
        String confirmarPassword
    ) {
        this.confirmarPassword = confirmarPassword;
    }

    public Set<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(
        Set<Long> rolesIds
    ) {
        this.rolesIds = rolesIds == null
            ? new LinkedHashSet<>()
            : rolesIds;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(
        boolean activo
    ) {
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
}