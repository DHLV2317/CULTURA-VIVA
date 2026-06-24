// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/controller/
// AdminAlertaController.java
// ==========================================================

package com.santaculturaviva.controller;

import com.santaculturaviva.dto.AlertaFormulario;
import com.santaculturaviva.service.AlertaService;

import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/alertas")
public class AdminAlertaController {

    private final AlertaService service;

    public AdminAlertaController(
        AlertaService service
    ) {
        this.service = service;
    }

    @InitBinder
    public void configurarBinder(
        WebDataBinder binder
    ) {
        binder.registerCustomEditor(
            String.class,
            new StringTrimmerEditor(true)
        );
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
            "alertas",
            service.listarTodas()
        );

        return "admin/alertas/index";
    }

    @GetMapping("/nueva")
    public String mostrarNueva(Model model) {
        prepararFormulario(
            model,
            new AlertaFormulario(),
            false,
            null
        );

        return "admin/alertas/formulario";
    }

    @PostMapping("/nueva")
    public String crear(
        @Valid
        @ModelAttribute("alertaFormulario")
        AlertaFormulario formulario,

        BindingResult bindingResult,

        Model model,

        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(
                model,
                formulario,
                false,
                null
            );

            return "admin/alertas/formulario";
        }

        service.crear(formulario);

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "La alerta fue creada correctamente."
        );

        return "redirect:/admin/alertas";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEdicion(
        @PathVariable Long id,
        Model model
    ) {
        prepararFormulario(
            model,
            service.obtenerFormulario(id),
            true,
            id
        );

        return "admin/alertas/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(
        @PathVariable Long id,

        @Valid
        @ModelAttribute("alertaFormulario")
        AlertaFormulario formulario,

        BindingResult bindingResult,

        Model model,

        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(
                model,
                formulario,
                true,
                id
            );

            return "admin/alertas/formulario";
        }

        service.actualizar(
            id,
            formulario
        );

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "La alerta fue actualizada correctamente."
        );

        return "redirect:/admin/alertas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {
        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "La alerta fue eliminada correctamente."
        );

        return "redirect:/admin/alertas";
    }

    private void prepararFormulario(
        Model model,
        AlertaFormulario formulario,
        boolean esEdicion,
        Long alertaId
    ) {
        model.addAttribute(
            "alertaFormulario",
            formulario
        );

        model.addAttribute(
            "esEdicion",
            esEdicion
        );

        model.addAttribute(
            "alertaId",
            alertaId
        );

        model.addAttribute(
            "tituloPagina",
            esEdicion
                ? "Editar alerta"
                : "Nueva alerta"
        );
    }
}