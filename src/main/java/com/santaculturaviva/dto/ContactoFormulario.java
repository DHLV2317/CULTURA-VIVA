
// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/dto/ContactoFormulario.java
// ==========================================================

package com.santaculturaviva.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactoFormulario {

    @NotBlank(
        message = "El nombre social es obligatorio."
    )
    @Size(
        max = 120,
        message = "El nombre social no puede superar los 120 caracteres."
    )
    private String nombreSocial;

    @Size(
        max = 50,
        message = "El pronombre no puede superar los 50 caracteres."
    )
    private String pronombre;

    @NotBlank(
        message = "El correo electrónico es obligatorio."
    )
    @Email(
        message = "Ingresa un correo electrónico válido."
    )
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres."
    )
    private String correo;

    @NotBlank(
        message = "El asunto es obligatorio."
    )
    @Size(
        max = 180,
        message = "El asunto no puede superar los 180 caracteres."
    )
    private String asunto;

    @NotBlank(
        message = "Selecciona un tipo de mensaje."
    )
    @Size(
        max = 60,
        message = "El tipo de mensaje no es válido."
    )
    private String tipoMensaje;

    @NotBlank(
        message = "El mensaje es obligatorio."
    )
    @Size(
        min = 10,
        max = 5000,
        message = "El mensaje debe tener entre 10 y 5000 caracteres."
    )
    private String mensaje;

    public ContactoFormulario() {
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

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}