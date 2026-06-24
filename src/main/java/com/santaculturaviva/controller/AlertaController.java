// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/controller/
// AlertaController.java
// ==========================================================

package com.santaculturaviva.controller;

import com.santaculturaviva.model.Alerta;
import com.santaculturaviva.service.AlertaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(
        AlertaService service
    ) {
        this.service = service;
    }

    @GetMapping
    public String listarAlertas(Model model) {
        List<Alerta> alertas =
            service.listarPublicadas();

        Alerta alertaDestacada = alertas
            .stream()
            .filter(Alerta::isDestacada)
            .findFirst()
            .orElseGet(
                () -> alertas.isEmpty()
                    ? null
                    : alertas.get(0)
            );

        List<Alerta> alertasSecundarias = alertas
            .stream()
            .filter(
                alerta -> alertaDestacada == null
                    || !alerta.getId().equals(
                        alertaDestacada.getId()
                    )
            )
            .toList();

        model.addAttribute(
            "alertas",
            alertas
        );

        model.addAttribute(
            "alertaDestacada",
            alertaDestacada
        );

        model.addAttribute(
            "alertasSecundarias",
            alertasSecundarias
        );

        return "alertas/index";
    }

    /*
     * Compatibilidad temporal con los enlaces antiguos
     * que todavía apuntan a /alertas/detalle.
     */
    @GetMapping("/detalle")
    public String redirigirDetalleAntiguo() {
        List<Alerta> alertas =
            service.listarPublicadas();

        if (alertas.isEmpty()) {
            return "redirect:/alertas";
        }

        return "redirect:/alertas/"
            + alertas.get(0).getSlug();
    }

    @GetMapping("/{slug}")
    public String mostrarDetalle(
        @PathVariable String slug,
        Model model
    ) {
        Alerta alerta =
            service.buscarPublicadaPorSlug(slug);

        model.addAttribute(
            "alerta",
            alerta
        );

        return "alertas/detalle";
    }
}