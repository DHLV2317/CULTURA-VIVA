package com.santaculturaviva.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

public class SuscripcionFormulario {

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
    @Pattern(
        regexp = "^[\\p{L}][\\p{L}\\s./'-]*$",
        message = "El pronombre solo puede contener letras, espacios, puntos, barras, guiones o apóstrofes."
    )
    private String pronombre;

    @NotBlank(
        message = "El correo electrónico es obligatorio."
    )
    @Email(
        message = "Ingresa un correo electrónico válido."
    )
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$",
        message = "El correo debe incluir un dominio válido, por ejemplo nombre@dominio.com."
    )
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres."
    )
    private String correo;

    @NotBlank(
        message = "Selecciona tu vínculo con la comunidad."
    )
    @Size(
        max = 80,
        message = "El tipo de vínculo seleccionado no es válido."
    )
    private String tipoVinculo;

    @NotEmpty(
        message = "Selecciona al menos un interés."
    )
    @Size(
        max = 7,
        message = "La cantidad de intereses seleccionada no es válida."
    )
    private Set<String> intereses = new LinkedHashSet<>();

    @NotBlank(
        message = "Selecciona la frecuencia del boletín."
    )
    @Size(
        max = 30,
        message = "La frecuencia seleccionada no es válida."
    )
    private String frecuencia;

    @Size(
        max = 2000,
        message = "El comentario no puede superar los 2000 caracteres."
    )
    private String comentario;

    @AssertTrue(
        message = "Debes autorizar el uso de tus datos para suscribirte."
    )
    private boolean consentimiento;

    public SuscripcionFormulario() {
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
}