param(
    [string]$Root = "C:\Users\Usuario\Documents\GitHub\CULTURA-VIVA"
)

$ErrorActionPreference = "Stop"

# ============================================================
# MIGRACIÓN COMPLETA DEL FRONTEND ESTÁTICO A THYMELEAF
# Migra todas las páginas restantes y conserva el Inicio actual.
# ============================================================

function Normalize-FullPath {
    param([Parameter(Mandatory = $true)][string]$Path)

    return [System.IO.Path]::GetFullPath($Path).TrimEnd("\")
}

function Get-RelativeFilePath {
    param(
        [Parameter(Mandatory = $true)][string]$BaseDirectory,
        [Parameter(Mandatory = $true)][string]$TargetPath
    )

    $base = (Normalize-FullPath $BaseDirectory) + "\"
    $baseUri = [System.Uri]::new($base)
    $targetUri = [System.Uri]::new((Normalize-FullPath $TargetPath))

    return [System.Uri]::UnescapeDataString(
        $baseUri.MakeRelativeUri($targetUri).ToString()
    ).Replace("/", "\")
}

function Split-ReferenceSuffix {
    param([Parameter(Mandatory = $true)][string]$Reference)

    $cutPositions = @()

    $questionIndex = $Reference.IndexOf("?")
    if ($questionIndex -ge 0) {
        $cutPositions += $questionIndex
    }

    $hashIndex = $Reference.IndexOf("#")
    if ($hashIndex -ge 0) {
        $cutPositions += $hashIndex
    }

    if ($cutPositions.Count -eq 0) {
        return @{
            PathPart = $Reference
            Suffix   = ""
        }
    }

    $cut = ($cutPositions | Measure-Object -Minimum).Minimum

    return @{
        PathPart = $Reference.Substring(0, $cut)
        Suffix   = $Reference.Substring($cut)
    }
}

function Is-ExternalReference {
    param([string]$Reference)

    if ([string]::IsNullOrWhiteSpace($Reference)) {
        return $true
    }

    return $Reference -match "^(?i)(https?:|mailto:|tel:|javascript:|data:|//)"
}

function Convert-InternalHref {
    param(
        [Parameter(Mandatory = $true)][string]$Href,
        [Parameter(Mandatory = $true)][string]$CurrentLegacyFile,
        [Parameter(Mandatory = $true)][hashtable]$RouteMap,
        [Parameter(Mandatory = $true)][string]$LegacyRoot
    )

    if (
        [string]::IsNullOrWhiteSpace($Href) -or
        $Href.StartsWith("#") -or
        (Is-ExternalReference $Href)
    ) {
        return @{
            Changed = $false
            Value   = $Href
            Route   = $null
        }
    }

    $parts = Split-ReferenceSuffix $Href
    $pathPart = $parts.PathPart
    $suffix = $parts.Suffix

    if ([string]::IsNullOrWhiteSpace($pathPart)) {
        return @{
            Changed = $false
            Value   = $Href
            Route   = $null
        }
    }

    $currentDirectory = Split-Path $CurrentLegacyFile -Parent

    if ($pathPart -eq "/") {
        $candidate = Join-Path $LegacyRoot "index.html"
    }
    elseif ([System.IO.Path]::IsPathRooted($pathPart)) {
        $candidate = Join-Path $LegacyRoot $pathPart.TrimStart("/", "\")
    }
    else {
        $candidate = Join-Path $currentDirectory $pathPart
    }

    if ($pathPart.EndsWith("/") -or (Test-Path $candidate -PathType Container)) {
        $candidate = Join-Path $candidate "index.html"
    }

    try {
        $fullCandidate = Normalize-FullPath $candidate
    }
    catch {
        return @{
            Changed = $false
            Value   = $Href
            Route   = $null
        }
    }

    if ($RouteMap.ContainsKey($fullCandidate)) {
        $route = $RouteMap[$fullCandidate]

        return @{
            Changed = $true
            Value   = $route + $suffix
            Route   = $route
        }
    }

    return @{
        Changed = $false
        Value   = $Href
        Route   = $null
    }
}

function Convert-LocalAsset {
    param(
        [Parameter(Mandatory = $true)][string]$Reference,
        [Parameter(Mandatory = $true)][string]$CurrentLegacyFile,
        [Parameter(Mandatory = $true)][string]$LegacyRoot,
        [Parameter(Mandatory = $true)][string]$StaticImgRoot
    )

    if (
        [string]::IsNullOrWhiteSpace($Reference) -or
        $Reference.StartsWith("#") -or
        (Is-ExternalReference $Reference)
    ) {
        return @{
            Changed = $false
            Value   = $Reference
            Public  = $null
        }
    }

    $parts = Split-ReferenceSuffix $Reference
    $pathPart = $parts.PathPart
    $suffix = $parts.Suffix

    $currentDirectory = Split-Path $CurrentLegacyFile -Parent

    if ([System.IO.Path]::IsPathRooted($pathPart)) {
        $candidate = Join-Path $LegacyRoot $pathPart.TrimStart("/", "\")
    }
    else {
        $candidate = Join-Path $currentDirectory $pathPart
    }

    $sourceExists = Test-Path $candidate -PathType Leaf

    if ($sourceExists) {
        $sourceFull = Normalize-FullPath $candidate
        $relative = Get-RelativeFilePath -BaseDirectory $LegacyRoot -TargetPath $sourceFull
        $relativeUnix = $relative.Replace("\", "/")

        if ($relativeUnix -match "^(?i)(img|images)/(.*)$") {
            $assetRelative = $Matches[2]
        }
        elseif ($relativeUnix -match "^(?i)assets/(img|images)/(.*)$") {
            $assetRelative = $Matches[2]
        }
        else {
            $assetRelative = "legacy/" + $relativeUnix
        }

        $assetRelative = $assetRelative.TrimStart("/")
        $targetFile = Join-Path $StaticImgRoot $assetRelative.Replace("/", "\")

        $targetDirectory = Split-Path $targetFile -Parent
        New-Item -ItemType Directory -Path $targetDirectory -Force | Out-Null

        Copy-Item $sourceFull $targetFile -Force

        $publicPath = "/img/" + $assetRelative

        return @{
            Changed = $true
            Value   = $publicPath + $suffix
            Public  = $publicPath
        }
    }

    $normalizedReference = $pathPart.Replace("\", "/")

    if ($normalizedReference -match "(?i)(?:^|/)(?:img|images)/(.*)$") {
        $publicPath = "/img/" + $Matches[1]

        return @{
            Changed = $true
            Value   = $publicPath + $suffix
            Public  = $publicPath
        }
    }

    return @{
        Changed = $false
        Value   = $Reference
        Public  = $null
    }
}

# ============================================================
# 1. RUTAS
# ============================================================

$Root = Normalize-FullPath $Root
$LegacyRoot = Join-Path $Root "santa-cultura-viva"
$ResourcesRoot = Join-Path $Root "src\main\resources"
$TemplatesRoot = Join-Path $ResourcesRoot "templates"
$StaticRoot = Join-Path $ResourcesRoot "static"
$StaticCssRoot = Join-Path $StaticRoot "css"
$StaticImgRoot = Join-Path $StaticRoot "img"
$ControllerPath = Join-Path $Root "src\main\java\com\santaculturaviva\controller\PageController.java"

Set-Location $Root
[Environment]::CurrentDirectory = $Root

$Utf8 = [System.Text.UTF8Encoding]::new($false)

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " MIGRACIÓN COMPLETA A SPRING BOOT + THYMELEAF" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Proyecto: $Root"
Write-Host "Frontend antiguo: $LegacyRoot"
Write-Host ""

foreach ($requiredPath in @($LegacyRoot, $TemplatesRoot, $StaticRoot, $StaticCssRoot)) {
    if (-not (Test-Path $requiredPath)) {
        throw "No existe la ruta requerida: $requiredPath"
    }
}

New-Item -ItemType Directory -Path $StaticImgRoot -Force | Out-Null
New-Item -ItemType Directory -Path (Split-Path $ControllerPath -Parent) -Force | Out-Null

# ============================================================
# 2. CONFIGURACIÓN DE PÁGINAS
# ============================================================

$Pages = @(
    [pscustomobject]@{
        Legacy    = "pages\fepuc\index.html"
        Target    = "fepuc\index.html"
        Route     = "/fepuc"
        Active    = "fepuc"
        Css       = "fepuc.css"
        BodyClass = "page-fepuc"
        Title     = "FEPUC | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\que-es.html"
        Target    = "que-es.html"
        Route     = "/que-es"
        Active    = ""
        Css       = "que-es.css"
        BodyClass = "page-que-es"
        Title     = "¿Qué es Santa Cultura Viva?"
    },
    [pscustomobject]@{
        Legacy    = "pages\alertas\index.html"
        Target    = "alertas\index.html"
        Route     = "/alertas"
        Active    = "alertas"
        Css       = "alertas.css"
        BodyClass = "page-alertas"
        Title     = "Alertas | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\alertas\detalle.html"
        Target    = "alertas\detalle.html"
        Route     = "/alertas/detalle"
        Active    = "alertas"
        Css       = "alertas.css"
        BodyClass = "page-alerta-detalle"
        Title     = "Detalle de alerta | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\cronicas\index.html"
        Target    = "cronicas\index.html"
        Route     = "/cronicas"
        Active    = "cronicas"
        Css       = "cronicas.css"
        BodyClass = "page-cronicas"
        Title     = "Crónicas | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\cronicas\detalle.html"
        Target    = "cronicas\detalle.html"
        Route     = "/cronicas/detalle"
        Active    = "cronicas"
        Css       = "cronicas.css"
        BodyClass = "page-cronica-detalle"
        Title     = "Detalle de crónica | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\curaduria\index.html"
        Target    = "curaduria\index.html"
        Route     = "/curaduria"
        Active    = "curaduria"
        Css       = "curaduria.css"
        BodyClass = "page-curaduria"
        Title     = "Curaduría | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\eventos\index.html"
        Target    = "eventos\index.html"
        Route     = "/eventos"
        Active    = "eventos"
        Css       = "eventos.css"
        BodyClass = "page-eventos"
        Title     = "Eventos | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\foranexs\index.html"
        Target    = "foranexs\index.html"
        Route     = "/foranexs"
        Active    = "foranexs"
        Css       = "foranexs.css"
        BodyClass = "page-foranexs"
        Title     = "Foránexs en Red | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\convocatorias\index.html"
        Target    = "convocatorias\index.html"
        Route     = "/convocatorias"
        Active    = "convocatorias"
        Css       = "convocatorias.css"
        BodyClass = "page-convocatorias"
        Title     = "Convocatorias | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\suscripcion.html"
        Target    = "suscripcion.html"
        Route     = "/suscripcion"
        Active    = ""
        Css       = "suscripcion.css"
        BodyClass = "page-suscripcion"
        Title     = "Suscripción | Santa Cultura Viva"
    },
    [pscustomobject]@{
        Legacy    = "pages\contacto\index.html"
        Target    = "contacto\index.html"
        Route     = "/contacto"
        Active    = ""
        Css       = "contacto.css"
        BodyClass = "page-contacto"
        Title     = "Contacto | Santa Cultura Viva"
    }
)

# ============================================================
# 3. MAPA DE RUTAS
# ============================================================

$RouteMap = @{}

$RouteMap[(Normalize-FullPath (Join-Path $LegacyRoot "index.html"))] = "/"

foreach ($page in $Pages) {
    $legacyFull = Normalize-FullPath (Join-Path $LegacyRoot $page.Legacy)
    $RouteMap[$legacyFull] = $page.Route
}

# ============================================================
# 4. COPIA DE SEGURIDAD
# ============================================================

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$BackupRoot = Join-Path $Root "migration-backup\all-pages-$timestamp"

New-Item -ItemType Directory -Path $BackupRoot -Force | Out-Null
Copy-Item $ResourcesRoot (Join-Path $BackupRoot "resources") -Recurse -Force

if (Test-Path $ControllerPath) {
    Copy-Item $ControllerPath (Join-Path $BackupRoot "PageController.java") -Force
}

Write-Host "Copia de seguridad creada en:" -ForegroundColor Green
Write-Host $BackupRoot
Write-Host ""

# ============================================================
# 5. PLANTILLA THYMELEAF
# ============================================================

$TemplateBase = @'
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    />

    <meta
        name="description"
        content="Santa Cultura Viva, revista digital universitaria sobre cultura, territorio, derechos humanos y comunidad."
    />

    <title>__TITLE__</title>

    <link
        rel="stylesheet"
        href="/css/styles.css"
        th:href="@{/css/styles.css}"
    />

    <link
        rel="stylesheet"
        href="/css/components.css"
        th:href="@{/css/components.css}"
    />

    <link
        rel="stylesheet"
        href="/css/__PAGE_CSS__"
        th:href="@{/css/__PAGE_CSS__}"
    />

    <link
        rel="stylesheet"
        href="/css/responsive.css"
        th:href="@{/css/responsive.css}"
    />
</head>

<body class="__BODY_CLASS__">
    <div th:replace="~{fragments/drawer :: drawer}"></div>

    <div
        th:replace="~{fragments/header :: header('__ACTIVE_PAGE__')}"
    ></div>

    <div th:replace="~{fragments/search :: search}"></div>

__MAIN_HTML__

    <div th:replace="~{fragments/footer :: footer}"></div>

    <script
        src="/js/main.js"
        th:src="@{/js/main.js}"
    ></script>

__INLINE_SCRIPTS__
</body>
</html>
'@

# ============================================================
# 6. MIGRAR PÁGINAS
# ============================================================

$CssBuckets = @{}
$Migrated = @()
$Warnings = @()
$Failures = @()

$HrefRegex = [regex]::new(
    "\bhref\s*=\s*(`"|')(.*?)\1",
    [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
)

$AssetRegex = [regex]::new(
    "\b(src|poster)\s*=\s*(`"|')(.*?)\2",
    [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
)

foreach ($page in $Pages) {
    try {
        $legacyFile = Normalize-FullPath (Join-Path $LegacyRoot $page.Legacy)
        $targetFile = Normalize-FullPath (Join-Path $TemplatesRoot $page.Target)

        Write-Host "Migrando $($page.Route)..." -ForegroundColor Cyan

        if (-not (Test-Path $legacyFile -PathType Leaf)) {
            throw "No se encontró el archivo fuente: $legacyFile"
        }

        $html = [System.IO.File]::ReadAllText($legacyFile, [System.Text.Encoding]::UTF8)

        $html = $html -replace "^\s*```html\s*", ""
        $html = $html -replace "\s*```\s*$", ""

        $mainMatch = [regex]::Match(
            $html,
            "(?is)<main\b[^>]*>.*?</main>"
        )

        if (-not $mainMatch.Success) {
            throw "No se encontró una etiqueta <main> completa."
        }

        $mainHtml = $mainMatch.Value.Trim()

        # Conservar clases existentes del body
        $legacyBodyClass = ""
        $bodyMatch = [regex]::Match(
            $html,
            "(?is)<body\b[^>]*\bclass\s*=\s*(`"|')(.*?)\1"
        )

        if ($bodyMatch.Success) {
            $legacyBodyClass = $bodyMatch.Groups[2].Value.Trim()
        }

        $bodyClass = ($page.BodyClass + " " + $legacyBodyClass).Trim()

        # Reescribir enlaces internos
        $mainHtml = $HrefRegex.Replace(
            $mainHtml,
            [System.Text.RegularExpressions.MatchEvaluator]{
                param($match)

                $href = $match.Groups[2].Value

                $converted = Convert-InternalHref `
                    -Href $href `
                    -CurrentLegacyFile $legacyFile `
                    -RouteMap $RouteMap `
                    -LegacyRoot $LegacyRoot

                if (-not $converted.Changed) {
                    return $match.Value
                }

                return 'href="' + $converted.Value + '" th:href="@{' + $converted.Route + '}"'
            }
        )

        # Reescribir y copiar imágenes/recursos locales
        $mainHtml = $AssetRegex.Replace(
            $mainHtml,
            [System.Text.RegularExpressions.MatchEvaluator]{
                param($match)

                $attribute = $match.Groups[1].Value.ToLowerInvariant()
                $reference = $match.Groups[3].Value

                $converted = Convert-LocalAsset `
                    -Reference $reference `
                    -CurrentLegacyFile $legacyFile `
                    -LegacyRoot $LegacyRoot `
                    -StaticImgRoot $StaticImgRoot

                if (-not $converted.Changed) {
                    return $match.Value
                }

                $thymeleafAttribute = if ($attribute -eq "src") { "th:src" } else { "th:attr" }

                if ($attribute -eq "src") {
                    return 'src="' + $converted.Value + '" th:src="@{' + $converted.Public + '}"'
                }

                return $attribute + '="' + $converted.Value + '"'
            }
        )

        # Extraer CSS interno de la página
        $styleMatches = [regex]::Matches(
            $html,
            "(?is)<style\b[^>]*>(.*?)</style>"
        )

        $pageCssParts = @()

        foreach ($styleMatch in $styleMatches) {
            $cssPart = $styleMatch.Groups[1].Value.Trim()

            if (-not [string]::IsNullOrWhiteSpace($cssPart)) {
                $pageCssParts += $cssPart
            }
        }

        if (-not $CssBuckets.ContainsKey($page.Css)) {
            $CssBuckets[$page.Css] = @()
        }

        $cssSection = @"
/* =========================================================
   Fuente: $($page.Legacy)
   Ruta: $($page.Route)
   ========================================================= */

"@

        if ($pageCssParts.Count -gt 0) {
            $cssSection += ($pageCssParts -join "`r`n`r`n")
        }
        else {
            $cssSection += "/* Esta página no contenía CSS interno. */"
        }

        $CssBuckets[$page.Css] += $cssSection

        # Conservar scripts inline, si existen
        $inlineScriptMatches = [regex]::Matches(
            $html,
            "(?is)<script\b(?![^>]*\bsrc\s*=)[^>]*>(.*?)</script>"
        )

        $inlineScripts = @()

        foreach ($scriptMatch in $inlineScriptMatches) {
            $scriptContent = $scriptMatch.Groups[1].Value.Trim()

            if (-not [string]::IsNullOrWhiteSpace($scriptContent)) {
                $inlineScripts += "<script>`r`n$scriptContent`r`n</script>"
            }
        }

        $inlineScriptsHtml = $inlineScripts -join "`r`n`r`n"

        # Crear plantilla final
        $newTemplate = $TemplateBase
        $newTemplate = $newTemplate.Replace("__TITLE__", $page.Title)
        $newTemplate = $newTemplate.Replace("__PAGE_CSS__", $page.Css)
        $newTemplate = $newTemplate.Replace("__BODY_CLASS__", $bodyClass)
        $newTemplate = $newTemplate.Replace("__ACTIVE_PAGE__", $page.Active)
        $newTemplate = $newTemplate.Replace("__MAIN_HTML__", $mainHtml)
        $newTemplate = $newTemplate.Replace("__INLINE_SCRIPTS__", $inlineScriptsHtml)

        $targetDirectory = Split-Path $targetFile -Parent
        New-Item -ItemType Directory -Path $targetDirectory -Force | Out-Null

        [System.IO.File]::WriteAllText(
            $targetFile,
            $newTemplate,
            $Utf8
        )

        # Advertencia conocida de contenido Foránexs
        if (
            $page.Route -eq "/foranexs" -and
            $mainHtml -match "(?i)\bFEPUC\b" -and
            $mainHtml -notmatch "(?i)For.{0,3}nexs"
        ) {
            $Warnings += "La página /foranexs parece contener contenido de FEPUC. Requiere revisión editorial."
        }

        if ($mainHtml -match "Ã|Â|ƒ") {
            $Warnings += "La página $($page.Route) todavía contiene posibles caracteres dañados."
        }

        $Migrated += $page.Route
        Write-Host "  [OK] $targetFile" -ForegroundColor Green
    }
    catch {
        $message = "$($page.Route): $($_.Exception.Message)"
        $Failures += $message
        Write-Host "  [ERROR] $message" -ForegroundColor Red
    }
}

# ============================================================
# 7. ESCRIBIR CSS POR PÁGINA
# ============================================================

Write-Host ""
Write-Host "Generando archivos CSS..." -ForegroundColor Cyan

foreach ($cssFileName in $CssBuckets.Keys) {
    $cssPath = Join-Path $StaticCssRoot $cssFileName

    $cssHeader = @"
/*
 * Santa Cultura Viva
 * Archivo generado durante la migración a Spring Boot.
 * Contiene estilos específicos de las páginas indicadas.
 */

"@

    $cssContent = $cssHeader + ($CssBuckets[$cssFileName] -join "`r`n`r`n")

    [System.IO.File]::WriteAllText(
        $cssPath,
        $cssContent,
        $Utf8
    )

    Write-Host "  [OK] $cssFileName" -ForegroundColor Green
}

# ============================================================
# 8. ACTUALIZAR PAGECONTROLLER
# ============================================================

$ControllerContent = @'
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
'@

[System.IO.File]::WriteAllText(
    $ControllerPath,
    $ControllerContent,
    $Utf8
)

Write-Host ""
Write-Host "[OK] PageController.java actualizado." -ForegroundColor Green

# ============================================================
# 9. AUDITORÍA
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " AUDITORÍA DE LA MIGRACIÓN" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

$AuditProblems = @()

foreach ($page in $Pages) {
    $targetFile = Join-Path $TemplatesRoot $page.Target

    if (-not (Test-Path $targetFile -PathType Leaf)) {
        $AuditProblems += "Falta plantilla: $($page.Target)"
        continue
    }

    if ((Get-Item $targetFile).Length -eq 0) {
        $AuditProblems += "Plantilla vacía: $($page.Target)"
    }

    $targetContent = [System.IO.File]::ReadAllText($targetFile)

    foreach ($fragmentName in @(
        "fragments/drawer",
        "fragments/header",
        "fragments/search",
        "fragments/footer"
    )) {
        if ($targetContent -notmatch [regex]::Escape($fragmentName)) {
            $AuditProblems += "$($page.Target) no incluye $fragmentName"
        }
    }

    if ($targetContent -match 'href="[^"]*\.html') {
        $AuditProblems += "$($page.Target) todavía contiene enlaces .html"
    }
}

$MojibakeMatches = Get-ChildItem `
    -Path $TemplatesRoot `
    -Recurse `
    -Filter "*.html" `
    -File |
Select-String -SimpleMatch -Pattern "Ãƒ", "Ã‚", "Â¿", "Ã¡", "Ã©", "Ã­", "Ã³", "Ãº"

if ($MojibakeMatches) {
    $AuditProblems += "Se detectaron posibles caracteres dañados en algunas plantillas."
}

Write-Host ""
Write-Host "Páginas migradas: $($Migrated.Count)/$($Pages.Count)" -ForegroundColor Green

foreach ($route in $Migrated) {
    Write-Host "  [OK] $route" -ForegroundColor Green
}

if ($Warnings.Count -gt 0) {
    Write-Host ""
    Write-Host "Advertencias:" -ForegroundColor Yellow

    foreach ($warning in ($Warnings | Select-Object -Unique)) {
        Write-Host "  - $warning" -ForegroundColor Yellow
    }
}

if ($AuditProblems.Count -gt 0) {
    Write-Host ""
    Write-Host "Problemas de auditoría:" -ForegroundColor Yellow

    foreach ($problem in ($AuditProblems | Select-Object -Unique)) {
        Write-Host "  - $problem" -ForegroundColor Yellow
    }
}
else {
    Write-Host ""
    Write-Host "Auditoría completada sin problemas estructurales." -ForegroundColor Green
}

if ($Failures.Count -gt 0) {
    Write-Host ""
    Write-Host "Páginas que no se pudieron migrar:" -ForegroundColor Red

    foreach ($failure in $Failures) {
        Write-Host "  - $failure" -ForegroundColor Red
    }

    Write-Host ""
    Write-Host "La migración terminó parcialmente. Revisa los errores anteriores." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host " MIGRACIÓN COMPLETA FINALIZADA" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Siguiente comando:" -ForegroundColor Cyan
Write-Host ".\mvnw.cmd clean spring-boot:run"
Write-Host ""
Write-Host "Luego prueba:" -ForegroundColor Cyan
Write-Host "http://localhost:8080"
Write-Host ""
Write-Host "Rutas a revisar:" -ForegroundColor Cyan

foreach ($page in $Pages) {
    Write-Host "http://localhost:8080$($page.Route)"
}
