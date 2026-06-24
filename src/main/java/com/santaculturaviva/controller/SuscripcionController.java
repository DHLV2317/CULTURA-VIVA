package com.santaculturaviva.controller;

import com.santaculturaviva.dto.SuscripcionFormulario;
import com.santaculturaviva.service.SuscriptorService;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suscripcion")
public class SuscripcionController {

    private final SuscriptorService service;

    public SuscripcionController(
        SuscriptorService service
    ) {
        this.service = service;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        if (
            !model.containsAttribute(
                "suscripcionFormulario"
            )
        ) {
            model.addAttribute(
                "suscripcionFormulario",
                new SuscripcionFormulario()
            );
        }

        return "suscripcion";
    }

    @PostMapping
    public String procesarFormulario(
        @Valid
        @ModelAttribute("suscripcionFormulario")
        SuscripcionFormulario formulario,

        BindingResult bindingResult,

        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "suscripcion";
        }

        if (
            service.correoRegistrado(
                formulario.getCorreo()
            )
        ) {
            bindingResult.rejectValue(
                "correo",
                "correo.duplicado",
                "Este correo electrónico ya está suscrito."
            );

            return "suscripcion";
        }

        try {
            service.guardar(formulario);
        } catch (
            DataIntegrityViolationException exception
        ) {
            bindingResult.rejectValue(
                "correo",
                "correo.duplicado",
                "Este correo electrónico ya está suscrito."
            );

            return "suscripcion";
        }

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "Tu suscripción fue registrada correctamente."
        );

        return "redirect:/suscripcion";
    }
}