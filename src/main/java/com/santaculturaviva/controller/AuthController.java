package com.santaculturaviva.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin(
        Authentication authentication
    ) {
        boolean tieneSesion =
            authentication != null
            && authentication.isAuthenticated()
            && !(authentication
                instanceof AnonymousAuthenticationToken);

        if (tieneSesion) {
            return "redirect:/panel";
        }

        return "auth/login";
    }

    @GetMapping("/panel")
    public String mostrarPanel(
        Authentication authentication,
        Model model
    ) {
        Set<String> roles = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        model.addAttribute(
            "correo",
            authentication.getName()
        );

        model.addAttribute(
            "esAdministrador",
            roles.contains("ROLE_ADMINISTRADOR")
        );

        model.addAttribute(
            "esEditor",
            roles.contains("ROLE_EDITOR")
        );

        model.addAttribute(
            "esRevisor",
            roles.contains("ROLE_REVISOR")
        );

        model.addAttribute(
            "esEstudiante",
            roles.contains(
                "ROLE_ESTUDIANTE_REGISTRADX"
            )
        );

        model.addAttribute(
            "roles",
            roles
        );

        return "auth/panel";
    }
}