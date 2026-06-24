package com.santaculturaviva.service;

import com.santaculturaviva.dto.SuscripcionFormulario;
import com.santaculturaviva.model.Suscriptor;
import com.santaculturaviva.repository.SuscriptorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class SuscriptorService {

    private final SuscriptorRepository repository;

    public SuscriptorService(
        SuscriptorRepository repository
    ) {
        this.repository = repository;
    }

    public boolean correoRegistrado(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }

        String correoLimpio =
            correo.trim().toLowerCase();

        return repository.existsByCorreoIgnoreCase(
            correoLimpio
        );
    }

    @Transactional
    public Suscriptor guardar(
        SuscripcionFormulario formulario
    ) {
        Suscriptor suscriptor = new Suscriptor();

        suscriptor.setNombreSocial(
            limpiar(formulario.getNombreSocial())
        );

        suscriptor.setPronombre(
            limpiarOpcional(formulario.getPronombre())
        );

        suscriptor.setCorreo(
            limpiar(formulario.getCorreo()).toLowerCase()
        );

        suscriptor.setTipoVinculo(
            limpiar(formulario.getTipoVinculo())
        );

        suscriptor.setIntereses(
            limpiarIntereses(formulario.getIntereses())
        );

        suscriptor.setFrecuencia(
            limpiar(formulario.getFrecuencia())
        );

        suscriptor.setComentario(
            limpiarOpcional(formulario.getComentario())
        );

        suscriptor.setConsentimiento(
            formulario.isConsentimiento()
        );

        suscriptor.setActivo(true);

        return repository.save(suscriptor);
    }

    private Set<String> limpiarIntereses(
        Set<String> intereses
    ) {
        Set<String> interesesLimpios =
            new LinkedHashSet<>();

        if (intereses == null) {
            return interesesLimpios;
        }

        for (String interes : intereses) {
            if (interes != null && !interes.isBlank()) {
                interesesLimpios.add(
                    interes.trim().toLowerCase()
                );
            }
        }

        return interesesLimpios;
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