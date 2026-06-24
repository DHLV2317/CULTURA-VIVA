// ==========================================================
// ARCHIVO:
// src/main/java/com/santaculturaviva/controller/
// ContactoController.java
// ==========================================================

package com.santaculturaviva.controller;

import com.santaculturaviva.dto.ContactoFormulario;
import com.santaculturaviva.service.MensajeContactoService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final MensajeContactoService service;

    public ContactoController(
        MensajeContactoService service
    ) {
        this.service = service;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        if (
            !model.containsAttribute(
                "contactoFormulario"
            )
        ) {
            model.addAttribute(
                "contactoFormulario",
                new ContactoFormulario()
            );
        }

        return "contacto/index";
    }

    @PostMapping
    public String procesarFormulario(
        @Valid
        @ModelAttribute("contactoFormulario")
        ContactoFormulario formulario,

        BindingResult bindingResult,

        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "contacto/index";
        }

        service.guardar(formulario);

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "Tu mensaje fue enviado correctamente. "
                + "Gracias por comunicarte con Santa Cultura Viva."
        );

        return "redirect:/contacto";
    }
}