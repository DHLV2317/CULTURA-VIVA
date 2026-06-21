param(
    [Parameter(Mandatory = $false)]
    [string]$ProjectRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  SANTA CULTURA VIVA - ESTRUCTURA SPRING BOOT" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

# -------------------------------------------------------
# VALIDAR LA CARPETA DEL PROYECTO
# -------------------------------------------------------

if (-not (Test-Path $ProjectRoot)) {
    Write-Error "La ruta indicada no existe: $ProjectRoot"
    exit 1
}

$ProjectRoot = (Resolve-Path $ProjectRoot).Path

Write-Host "Proyecto seleccionado:" -ForegroundColor Yellow
Write-Host $ProjectRoot
Write-Host ""

$projectName = Split-Path $ProjectRoot -Leaf

if ($projectName -ne "CULTURA-VIVA") {
    Write-Warning "La carpeta seleccionada se llama '$projectName' y no 'CULTURA-VIVA'."
    Write-Warning "La estructura se creará igualmente dentro de esa carpeta."
    Write-Host ""
}

# -------------------------------------------------------
# FUNCIÓN PARA CREAR ARCHIVOS SIN SOBRESCRIBIR
# -------------------------------------------------------

function New-FileIfMissing {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RelativePath,

        [Parameter(Mandatory = $false)]
        [string]$Content = ""
    )

    $fullPath = Join-Path $ProjectRoot $RelativePath
    $parentDirectory = Split-Path $fullPath -Parent

    if (-not (Test-Path $parentDirectory)) {
        New-Item `
            -ItemType Directory `
            -Path $parentDirectory `
            -Force | Out-Null
    }

    if (-not (Test-Path $fullPath)) {
        [System.IO.File]::WriteAllText(
            $fullPath,
            $Content,
            [System.Text.UTF8Encoding]::new($false)
        )

        Write-Host "[CREADO]  $RelativePath" -ForegroundColor Green
    }
    else {
        Write-Host "[EXISTE]  $RelativePath" -ForegroundColor DarkYellow
    }
}

# -------------------------------------------------------
# CREAR DIRECTORIOS
# -------------------------------------------------------

$directories = @(
    # Java
    "src/main/java/com/santaculturaviva",
    "src/main/java/com/santaculturaviva/controller",
    "src/main/java/com/santaculturaviva/service",
    "src/main/java/com/santaculturaviva/repository",
    "src/main/java/com/santaculturaviva/model",
    "src/main/java/com/santaculturaviva/dto",
    "src/main/java/com/santaculturaviva/config",

    # Templates
    "src/main/resources/templates",
    "src/main/resources/templates/fragments",
    "src/main/resources/templates/alertas",
    "src/main/resources/templates/cronicas",
    "src/main/resources/templates/curaduria",
    "src/main/resources/templates/eventos",
    "src/main/resources/templates/fepuc",
    "src/main/resources/templates/foranexs",
    "src/main/resources/templates/convocatorias",
    "src/main/resources/templates/contacto",

    # Archivos estáticos
    "src/main/resources/static/css",
    "src/main/resources/static/js",
    "src/main/resources/static/img",

    # Tests
    "src/test/java/com/santaculturaviva",
    "src/test/resources"
)

Write-Host "Creando directorios..." -ForegroundColor Cyan
Write-Host ""

foreach ($directory in $directories) {
    $fullDirectoryPath = Join-Path $ProjectRoot $directory

    if (-not (Test-Path $fullDirectoryPath)) {
        New-Item `
            -ItemType Directory `
            -Path $fullDirectoryPath `
            -Force | Out-Null

        Write-Host "[CREADO]  $directory" -ForegroundColor Green
    }
    else {
        Write-Host "[EXISTE]  $directory" -ForegroundColor DarkYellow
    }
}

Write-Host ""
Write-Host "Creando archivos iniciales..." -ForegroundColor Cyan
Write-Host ""

# -------------------------------------------------------
# CLASE PRINCIPAL DE SPRING BOOT
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/java/com/santaculturaviva/CulturaVivaApplication.java" `
@'
package com.santaculturaviva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CulturaVivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CulturaVivaApplication.class, args);
    }
}
'@

# -------------------------------------------------------
# CONTROLADOR INICIAL DE PÁGINAS
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/java/com/santaculturaviva/controller/PageController.java" `
@'
package com.santaculturaviva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/que-es")
    public String queEs() {
        return "que-es";
    }

    @GetMapping("/suscripcion")
    public String suscripcion() {
        return "suscripcion";
    }

    @GetMapping("/fepuc")
    public String fepuc() {
        return "fepuc/index";
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

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto/index";
    }
}
'@

# -------------------------------------------------------
# FRAGMENTOS THYMELEAF
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/resources/templates/fragments/header.html" `
@'
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<body>

<header
    class="editorial-header"
    th:fragment="header(activePage)"
>
    <!-- Cabecera editorial compartida -->

    <div class="editorial-masthead">
        <div class="masthead-tools">
            <button
                class="icon-button hamburger-button"
                id="editorialMenuBtn"
                type="button"
                aria-label="Abrir menú"
                aria-expanded="false"
                aria-controls="editorialDrawer"
            >
                <span></span>
                <span></span>
                <span></span>
            </button>

            <button
                class="icon-button search-button"
                id="searchButton"
                type="button"
                aria-label="Abrir buscador"
                aria-expanded="false"
                aria-controls="searchPanel"
            >
                <span class="search-circle"></span>
                <span class="search-handle"></span>
            </button>
        </div>

        <a class="editorial-brand" th:href="@{/}">
            <span class="brand-name">
                Santa Cultura Viva
            </span>
        </a>

        <div class="masthead-social">
            <a href="#" aria-label="Instagram">IG</a>
            <a href="#" aria-label="Facebook">FB</a>
            <a href="#" aria-label="TikTok">TK</a>
            <a href="#" aria-label="YouTube">YT</a>
        </div>
    </div>

    <nav class="editorial-nav" aria-label="Navegación principal">
        <a
            th:href="@{/}"
            th:classappend="${activePage == 'inicio'} ? ' active' : ''"
        >
            Inicio
        </a>

        <a
            th:href="@{/fepuc}"
            th:classappend="${activePage == 'fepuc'} ? ' active' : ''"
        >
            FEPUC
        </a>

        <a
            th:href="@{/alertas}"
            th:classappend="${activePage == 'alertas'} ? ' active' : ''"
        >
            Alertas
        </a>

        <a
            th:href="@{/cronicas}"
            th:classappend="${activePage == 'cronicas'} ? ' active' : ''"
        >
            Crónicas
        </a>

        <a
            th:href="@{/curaduria}"
            th:classappend="${activePage == 'curaduria'} ? ' active' : ''"
        >
            Curaduría
        </a>

        <a
            th:href="@{/eventos}"
            th:classappend="${activePage == 'eventos'} ? ' active' : ''"
        >
            Eventos
        </a>

        <a
            th:href="@{/foranexs}"
            th:classappend="${activePage == 'foranexs'} ? ' active' : ''"
        >
            Foránexs en Red
        </a>

        <a
            th:href="@{/convocatorias}"
            th:classappend="${activePage == 'convocatorias'} ? ' active' : ''"
        >
            Convocatorias
        </a>
    </nav>
</header>

</body>
</html>
'@

New-FileIfMissing `
    "src/main/resources/templates/fragments/drawer.html" `
@'
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<body>

<div th:fragment="drawer">
    <div
        class="drawer-backdrop"
        id="drawerBackdrop"
        aria-hidden="true"
    ></div>

    <aside
        class="editorial-drawer"
        id="editorialDrawer"
        aria-hidden="true"
    >
        <div class="drawer-header">
            <strong>Santa Cultura Viva</strong>

            <button
                class="drawer-close"
                id="drawerClose"
                type="button"
                aria-label="Cerrar menú"
            >
                ×
            </button>
        </div>

        <nav class="drawer-nav" aria-label="Navegación lateral">
            <a th:href="@{/}">Inicio</a>
            <a th:href="@{/fepuc}">FEPUC</a>
            <a th:href="@{/que-es}">¿Qué es Santa Cultura Viva?</a>
            <a th:href="@{/alertas}">Alertas</a>
            <a th:href="@{/cronicas}">Crónicas</a>
            <a th:href="@{/curaduria}">Curaduría</a>
            <a th:href="@{/eventos}">Eventos</a>
            <a th:href="@{/foranexs}">Foránexs en Red</a>
            <a th:href="@{/convocatorias}">Convocatorias</a>
            <a th:href="@{/suscripcion}">Suscripción</a>
            <a th:href="@{/contacto}">Contacto</a>
        </nav>
    </aside>
</div>

</body>
</html>
'@

New-FileIfMissing `
    "src/main/resources/templates/fragments/search.html" `
@'
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<body>

<section
    class="search-panel"
    id="searchPanel"
    aria-label="Buscador de Santa Cultura Viva"
    th:fragment="search"
>
    <div class="search-panel-content">
        <label for="siteSearch">
            Buscar en Santa Cultura Viva
        </label>

        <div class="search-input-row">
            <input
                id="siteSearch"
                type="search"
                placeholder="Busca alertas, crónicas, eventos o recursos"
            />

            <button
                id="searchSubmit"
                type="button"
                class="search-submit"
            >
                Buscar
            </button>
        </div>
    </div>
</section>

</body>
</html>
'@

New-FileIfMissing `
    "src/main/resources/templates/fragments/footer.html" `
@'
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<body>

<footer
    class="footer-editorial"
    th:fragment="footer"
>
    <div class="container footer-editorial-grid">
        <div class="footer-editorial-brand">
            <a th:href="@{/}">
                Santa Cultura Viva
            </a>

            <p>
                Revista digital universitaria sobre cultura,
                territorio, derechos humanos y comunidad.
            </p>
        </div>

        <div class="footer-editorial-column">
            <h3>Proyecto</h3>

            <p>
                Una plataforma editorial que visibiliza voces,
                experiencias y problemáticas universitarias.
            </p>

            <a th:href="@{/que-es}">
                Conocer el proyecto
            </a>
        </div>

        <div class="footer-editorial-column">
            <h3>Contenidos</h3>

            <a th:href="@{/alertas}">Alertas</a>
            <a th:href="@{/cronicas}">Crónicas</a>
            <a th:href="@{/curaduria}">Curaduría</a>
            <a th:href="@{/eventos}">Eventos</a>
        </div>

        <div class="footer-editorial-column">
            <h3>Comunidad</h3>

            <a th:href="@{/fepuc}">FEPUC</a>
            <a th:href="@{/foranexs}">Foránexs en Red</a>
            <a th:href="@{/convocatorias}">Convocatorias</a>
            <a th:href="@{/contacto}">Contacto</a>
        </div>

        <div class="footer-editorial-column">
            <h3>Newsletter</h3>

            <p>
                Recibe publicaciones y novedades
                de Santa Cultura Viva.
            </p>

            <a th:href="@{/suscripcion}">
                Ir a suscripción
            </a>
        </div>
    </div>

    <div class="container footer-editorial-bottom">
        <p>
            © 2026 Santa Cultura Viva.
            Todos los derechos reservados.
        </p>
    </div>
</footer>

</body>
</html>
'@

# -------------------------------------------------------
# PLANTILLA HTML PROVISIONAL
# -------------------------------------------------------

$basicTemplate = @'
<!DOCTYPE html>
<html
    lang="es"
    xmlns:th="http://www.thymeleaf.org"
>
<head>
    <meta charset="UTF-8" />

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    />

    <title>Santa Cultura Viva</title>

    <link
        rel="stylesheet"
        th:href="@{/css/styles.css}"
    />

    <link
        rel="stylesheet"
        th:href="@{/css/components.css}"
    />

    <link
        rel="stylesheet"
        th:href="@{/css/responsive.css}"
    />
</head>

<body>
    <div
        th:replace="~{fragments/drawer :: drawer}"
    ></div>

    <div
        th:replace="~{fragments/header :: header('inicio')}"
    ></div>

    <div
        th:replace="~{fragments/search :: search}"
    ></div>

    <main class="container">
        <h1>Santa Cultura Viva</h1>

        <p>
            Plantilla provisional. Aquí se migrará
            el contenido del frontend original.
        </p>
    </main>

    <div
        th:replace="~{fragments/footer :: footer}"
    ></div>

    <script th:src="@{/js/main.js}"></script>
</body>
</html>
'@

# -------------------------------------------------------
# TEMPLATES PRINCIPALES
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/resources/templates/index.html" `
    $basicTemplate

New-FileIfMissing `
    "src/main/resources/templates/que-es.html" `
    "<!-- Pendiente migrar la página ¿Qué es Santa Cultura Viva? -->"

New-FileIfMissing `
    "src/main/resources/templates/suscripcion.html" `
    "<!-- Pendiente migrar la página de Suscripción -->"

New-FileIfMissing `
    "src/main/resources/templates/alertas/index.html" `
    "<!-- Pendiente migrar el listado de Alertas -->"

New-FileIfMissing `
    "src/main/resources/templates/alertas/detalle.html" `
    "<!-- Pendiente migrar el detalle de Alertas -->"

New-FileIfMissing `
    "src/main/resources/templates/cronicas/index.html" `
    "<!-- Pendiente migrar el listado de Crónicas -->"

New-FileIfMissing `
    "src/main/resources/templates/cronicas/detalle.html" `
    "<!-- Pendiente migrar el detalle de Crónicas -->"

New-FileIfMissing `
    "src/main/resources/templates/curaduria/index.html" `
    "<!-- Pendiente migrar la página de Curaduría -->"

New-FileIfMissing `
    "src/main/resources/templates/eventos/index.html" `
    "<!-- Pendiente migrar la página de Eventos -->"

New-FileIfMissing `
    "src/main/resources/templates/fepuc/index.html" `
    "<!-- Pendiente migrar la página de FEPUC -->"

New-FileIfMissing `
    "src/main/resources/templates/foranexs/index.html" `
    "<!-- Pendiente migrar la página de Foránexs en Red -->"

New-FileIfMissing `
    "src/main/resources/templates/convocatorias/index.html" `
    "<!-- Pendiente migrar la página de Convocatorias -->"

New-FileIfMissing `
    "src/main/resources/templates/contacto/index.html" `
    "<!-- Pendiente migrar la página de Contacto -->"

# -------------------------------------------------------
# CSS
# -------------------------------------------------------

$cssFiles = @{
    "styles.css" = @'
/*
 * Santa Cultura Viva
 * Estilos base: variables, reset, tipografía y utilidades.
 */
'@

    "components.css" = @'
/*
 * Componentes compartidos:
 * header, drawer, buscador, botones, tarjetas y footer.
 */
'@

    "home.css" = @'
/*
 * Estilos exclusivos de la página principal.
 */
'@

    "alertas.css" = @'
/*
 * Estilos del listado y detalle de Alertas.
 */
'@

    "cronicas.css" = @'
/*
 * Estilos del listado y detalle de Crónicas.
 */
'@

    "curaduria.css" = @'
/*
 * Estilos de la sección Curaduría.
 */
'@

    "eventos.css" = @'
/*
 * Estilos de la sección Eventos.
 */
'@

    "fepuc.css" = @'
/*
 * Estilos de la sección FEPUC.
 */
'@

    "foranexs.css" = @'
/*
 * Estilos de la sección Foránexs en Red.
 */
'@

    "formularios.css" = @'
/*
 * Estilos de formularios de Contacto y Suscripción.
 */
'@

    "responsive.css" = @'
/*
 * Reglas responsive compartidas.
 */
'@
}

foreach ($cssFile in $cssFiles.GetEnumerator()) {
    New-FileIfMissing `
        "src/main/resources/static/css/$($cssFile.Key)" `
        $cssFile.Value
}

# -------------------------------------------------------
# JAVASCRIPT
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/resources/static/js/main.js" `
@'
"use strict";

/*
 * JavaScript general de Santa Cultura Viva.
 * Aquí se gestionarán:
 * - menú lateral;
 * - buscador;
 * - filtros;
 * - validaciones visuales.
 */

document.addEventListener("DOMContentLoaded", function () {
    console.log("Santa Cultura Viva cargó correctamente.");
});
'@

# -------------------------------------------------------
# CARPETA DE IMÁGENES
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/resources/static/img/.gitkeep" `
    ""

# -------------------------------------------------------
# CONFIGURACIÓN INICIAL
# -------------------------------------------------------

New-FileIfMissing `
    "src/main/resources/application.properties" `
@'
spring.application.name=cultura-viva

server.port=8080

# Desactivar caché durante el desarrollo
spring.thymeleaf.cache=false

# Mostrar mensajes de error útiles durante desarrollo
server.error.include-message=always

# -------------------------------------------------------
# MYSQL - SE CONFIGURARÁ EN LA SIGUIENTE ETAPA
# -------------------------------------------------------

# spring.datasource.url=jdbc:mysql://localhost:3306/cultura_viva
# spring.datasource.username=root
# spring.datasource.password=TU_CONTRASENA
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
#
# spring.jpa.hibernate.ddl-auto=update
# spring.jpa.show-sql=true
# spring.jpa.properties.hibernate.format_sql=true
'@

# -------------------------------------------------------
# TEST PROVISIONAL
# -------------------------------------------------------

New-FileIfMissing `
    "src/test/java/com/santaculturaviva/CulturaVivaApplicationTests.java" `
@'
package com.santaculturaviva;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CulturaVivaApplicationTests {

    @Test
    void contextLoads() {
    }
}
'@

# -------------------------------------------------------
# COMPROBAR ARCHIVOS DEL PROYECTO MAVEN
# -------------------------------------------------------

Write-Host ""
Write-Host "Comprobando archivos Maven..." -ForegroundColor Cyan
Write-Host ""

$mavenFiles = @(
    "pom.xml",
    "mvnw",
    "mvnw.cmd"
)

$missingMavenFiles = @()

foreach ($mavenFile in $mavenFiles) {
    $fullMavenPath = Join-Path $ProjectRoot $mavenFile

    if (Test-Path $fullMavenPath) {
        Write-Host "[CORRECTO] $mavenFile" -ForegroundColor Green
    }
    else {
        Write-Host "[FALTA]    $mavenFile" -ForegroundColor Red
        $missingMavenFiles += $mavenFile
    }
}

Write-Host ""

if ($missingMavenFiles.Count -gt 0) {
    Write-Warning "Faltan archivos base de Maven/Spring Boot."
    Write-Warning "No se crearon vacíos porque deben obtenerse desde Spring Initializr."
    Write-Warning "Genera el proyecto base y copia pom.xml, mvnw, mvnw.cmd y la carpeta .mvn."
}
else {
    Write-Host "Los archivos base de Maven están disponibles." -ForegroundColor Green
}

# -------------------------------------------------------
# RESULTADO
# -------------------------------------------------------

Write-Host ""
Write-Host "=============================================" -ForegroundColor Green
Write-Host " ESTRUCTURA CREADA CORRECTAMENTE" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Ruta del proyecto:" -ForegroundColor Cyan
Write-Host $ProjectRoot

Write-Host ""
Write-Host "Para revisar la estructura ejecuta:" -ForegroundColor Cyan
Write-Host "tree src /F"

Write-Host ""
Write-Host "Cuando pom.xml y Maven Wrapper estén disponibles:" -ForegroundColor Cyan
Write-Host ".\mvnw.cmd spring-boot:run"

Write-Host ""