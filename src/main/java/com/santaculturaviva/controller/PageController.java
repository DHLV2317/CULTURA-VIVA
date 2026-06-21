package com.santaculturaviva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/fepuc")
    public String fepuc() {
        return "fepuc/index";
    }

    @GetMapping("/que-es")
    public String queEs() {
        return "que-es";
    }

    @GetMapping("/alertas")
    public String alertas() {
        return "alertas/index";
    }

    @GetMapping("/alertas/detalle")
    public String detalleAlerta() {
        return "alertas/detalle";
    }

    @GetMapping("/cronicas")
    public String cronicas() {
        return "cronicas/index";
    }

    @GetMapping("/cronicas/detalle")
    public String detalleCronica() {
        return "cronicas/detalle";
    }

    @GetMapping("/curaduria")
    public String curaduria() {
        return "curaduria/index";
    }

    @GetMapping("/eventos")
    public String eventos() {
        return "eventos/index";
    }

    @GetMapping("/foranexs")
    public String foranexs() {
        return "foranexs/index";
    }

    @GetMapping("/convocatorias")
    public String convocatorias() {
        return "convocatorias/index";
    }

    @GetMapping("/suscripcion")
    public String suscripcion() {
        return "suscripcion";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto/index";
    }
}