
// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/service/
// MensajeContactoService.java
// ==========================================================

package com.santaculturaviva.service;

import com.santaculturaviva.dto.ContactoFormulario;
import com.santaculturaviva.model.MensajeContacto;
import com.santaculturaviva.repository.MensajeContactoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repository;

    public MensajeContactoService(
        MensajeContactoRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public MensajeContacto guardar(
        ContactoFormulario formulario
    ) {
        MensajeContacto mensajeContacto =
            new MensajeContacto();

        mensajeContacto.setNombreSocial(
            limpiar(formulario.getNombreSocial())
        );

        mensajeContacto.setPronombre(
            limpiarOpcional(formulario.getPronombre())
        );

        mensajeContacto.setCorreo(
            limpiar(formulario.getCorreo()).toLowerCase()
        );

        mensajeContacto.setAsunto(
            limpiar(formulario.getAsunto())
        );

        mensajeContacto.setTipoMensaje(
            limpiar(formulario.getTipoMensaje())
        );

        mensajeContacto.setMensaje(
            limpiar(formulario.getMensaje())
        );

        mensajeContacto.setEstado("NUEVO");

        return repository.save(mensajeContacto);
    }

    private String limpiar(String valor) {
        return valor == null
            ? ""
            : valor.trim();
    }

    private String limpiarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}