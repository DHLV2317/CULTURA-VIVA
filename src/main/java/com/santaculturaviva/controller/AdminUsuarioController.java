package com.santaculturaviva.controller;

import com.santaculturaviva.dto.UsuarioFormulario;
import com.santaculturaviva.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
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

import java.util.Objects;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(
        UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
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

    @ModelAttribute("rolesDisponibles")
    public Object rolesDisponibles() {
        return usuarioService.listarRolesActivos();
    }

    @GetMapping
    public String listarUsuarios(
        Model model
    ) {
        model.addAttribute(
            "usuarios",
            usuarioService.listarUsuarios()
        );

        return "admin/usuarios/index";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(
        Model model
    ) {
        UsuarioFormulario formulario =
            new UsuarioFormulario();

        formulario.setActivo(true);
        formulario.setCorreoVerificado(true);
        formulario.setBloqueado(false);

        prepararFormulario(
            model,
            formulario,
            false,
            null
        );

        return "admin/usuarios/formulario";
    }

    @PostMapping("/nuevo")
    public String crearUsuario(
        @Valid
        @ModelAttribute("usuarioFormulario")
        UsuarioFormulario formulario,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        validarFormulario(
            formulario,
            bindingResult,
            null,
            true
        );

        if (bindingResult.hasErrors()) {
            prepararFormulario(
                model,
                formulario,
                false,
                null
            );

            return "admin/usuarios/formulario";
        }

        usuarioService.crearUsuario(
            formulario
        );

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "El usuario fue creado correctamente."
        );

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEdicion(
        @PathVariable Long id,
        Model model
    ) {
        UsuarioFormulario formulario =
            usuarioService.obtenerFormulario(id);

        prepararFormulario(
            model,
            formulario,
            true,
            id
        );

        return "admin/usuarios/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizarUsuario(
        @PathVariable Long id,
        @Valid
        @ModelAttribute("usuarioFormulario")
        UsuarioFormulario formulario,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        validarFormulario(
            formulario,
            bindingResult,
            id,
            false
        );

        if (bindingResult.hasErrors()) {
            prepararFormulario(
                model,
                formulario,
                true,
                id
            );

            return "admin/usuarios/formulario";
        }

        usuarioService.actualizarUsuario(
            id,
            formulario
        );

        redirectAttributes.addFlashAttribute(
            "mensajeExito",
            "Los datos del usuario fueron actualizados."
        );

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.cambiarEstado(
                id,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "El estado del usuario fue actualizado."
            );
        }
        catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(
                "mensajeError",
                exception.getMessage()
            );
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/bloqueo")
    public String cambiarBloqueo(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.cambiarBloqueo(
                id,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "El bloqueo del usuario fue actualizado."
            );
        }
        catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(
                "mensajeError",
                exception.getMessage()
            );
        }

        return "redirect:/admin/usuarios";
    }

    private void validarFormulario(
        UsuarioFormulario formulario,
        BindingResult bindingResult,
        Long usuarioId,
        boolean esCreacion
    ) {
        if (
            usuarioService.correoEnUso(
                formulario.getCorreo(),
                usuarioId
            )
        ) {
            bindingResult.rejectValue(
                "correo",
                "correo.duplicado",
                "Ya existe una cuenta con este correo."
            );
        }

        boolean passwordVacia =
            formulario.getPassword() == null
            || formulario.getPassword().isBlank();

        boolean confirmacionVacia =
            formulario.getConfirmarPassword() == null
            || formulario
                .getConfirmarPassword()
                .isBlank();

        if (
            esCreacion
            && passwordVacia
        ) {
            bindingResult.rejectValue(
                "password",
                "password.obligatoria",
                "La contraseña es obligatoria."
            );
        }

        if (
            !passwordVacia
            && formulario.getPassword().length() < 8
        ) {
            bindingResult.rejectValue(
                "password",
                "password.corta",
                "La contraseña debe tener al menos 8 caracteres."
            );
        }

        if (
            !passwordVacia
            && !Objects.equals(
                formulario.getPassword(),
                formulario.getConfirmarPassword()
            )
        ) {
            bindingResult.rejectValue(
                "confirmarPassword",
                "password.no.coincide",
                "Las contraseñas no coinciden."
            );
        }

        if (
            passwordVacia
            && !confirmacionVacia
        ) {
            bindingResult.rejectValue(
                "password",
                "password.faltante",
                "Escribe la nueva contraseña."
            );
        }
    }

    private void prepararFormulario(
        Model model,
        UsuarioFormulario formulario,
        boolean esEdicion,
        Long usuarioId
    ) {
        model.addAttribute(
            "usuarioFormulario",
            formulario
        );

        model.addAttribute(
            "esEdicion",
            esEdicion
        );

        model.addAttribute(
            "usuarioId",
            usuarioId
        );

        model.addAttribute(
            "tituloPagina",
            esEdicion
                ? "Editar usuario"
                : "Nuevo usuario"
        );
    }
}