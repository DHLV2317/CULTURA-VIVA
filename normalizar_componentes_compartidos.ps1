param(
    [string]$Root = "C:\Users\Usuario\Documents\GitHub\CULTURA-VIVA"
)

$ErrorActionPreference = "Stop"

function Get-FullPath {
    param([Parameter(Mandatory = $true)][string]$Path)

    return [System.IO.Path]::GetFullPath($Path)
}

$Root = Get-FullPath $Root
$TemplatesRoot = Join-Path $Root "src\main\resources\templates"
$CssRoot = Join-Path $Root "src\main\resources\static\css"
$ComponentsPath = Join-Path $CssRoot "components.css"
$Utf8 = [System.Text.UTF8Encoding]::new($false)

Set-Location $Root
[Environment]::CurrentDirectory = $Root

foreach ($requiredPath in @($TemplatesRoot, $CssRoot)) {
    if (-not (Test-Path $requiredPath)) {
        throw "No existe la ruta requerida: $requiredPath"
    }
}

if (-not (Test-Path $ComponentsPath)) {
    [System.IO.File]::WriteAllText(
        $ComponentsPath,
        "/* Componentes compartidos de Santa Cultura Viva */`r`n",
        $Utf8
    )
}

# ============================================================
# 1. COPIA DE SEGURIDAD
# ============================================================

$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$BackupRoot = Join-Path $Root "migration-backup\shared-components-$Timestamp"

New-Item -ItemType Directory -Path $BackupRoot -Force | Out-Null
Copy-Item $ComponentsPath (Join-Path $BackupRoot "components.css") -Force
Copy-Item $TemplatesRoot (Join-Path $BackupRoot "templates") -Recurse -Force

Write-Host ""
Write-Host "Copia de seguridad:" -ForegroundColor Green
Write-Host $BackupRoot

# ============================================================
# 2. NORMALIZAR FOOTER EN COMPONENTS.CSS
# ============================================================

$StartMarker = "/* SCV_SHARED_FOOTER_START */"
$EndMarker = "/* SCV_SHARED_FOOTER_END */"

$FooterCss = @'
/* SCV_SHARED_FOOTER_START */

/* Footer editorial compartido: debe verse igual en todas las rutas. */
.footer-editorial {
    width: 100%;
    margin: 0;
    padding: 5rem 0 2rem;
    background: #1c1a19;
    color: #ffffff;
    font-family: Arial, Helvetica, sans-serif;
}

.footer-editorial .footer-editorial-grid {
    display: grid;
    grid-template-columns: 1.4fr repeat(4, minmax(0, 1fr));
    gap: 3rem;
    align-items: start;
}

.footer-editorial .footer-editorial-brand > a {
    display: inline-block;
    margin-bottom: 1rem;
    color: #ffffff;
    font-family: Georgia, "Times New Roman", serif;
    font-size: 2rem;
    font-weight: 700;
    line-height: 1.15;
    text-decoration: none;
}

.footer-editorial .footer-editorial-brand p,
.footer-editorial .footer-editorial-column p {
    margin: 0 0 1rem;
    color: #aaa5a1;
    font-size: 0.9rem;
    line-height: 1.55;
}

.footer-editorial .footer-editorial-column h3 {
    margin: 0 0 1rem;
    color: #ffffff;
    font-family: Arial, Helvetica, sans-serif;
    font-size: 0.78rem;
    font-weight: 800;
    letter-spacing: 0.07em;
    line-height: 1.3;
    text-transform: uppercase;
}

.footer-editorial .footer-editorial-column a {
    display: block;
    width: fit-content;
    margin: 0 0 0.65rem;
    color: #d2ceca;
    font-family: Arial, Helvetica, sans-serif;
    font-size: 0.9rem;
    line-height: 1.45;
    text-decoration: none;
}

.footer-editorial .footer-editorial-column a:hover,
.footer-editorial .footer-editorial-column a:focus-visible {
    color: #ffffff;
    text-decoration: underline;
}

.footer-editorial .footer-editorial-bottom {
    margin-top: 4rem;
    padding-top: 1.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.18);
}

.footer-editorial .footer-editorial-bottom p {
    margin: 0;
    color: #aaa5a1;
    font-family: Arial, Helvetica, sans-serif;
    font-size: 0.82rem;
    line-height: 1.5;
}

@media (max-width: 1100px) {
    .footer-editorial .footer-editorial-grid {
        grid-template-columns: 1.3fr repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 850px) {
    .footer-editorial .footer-editorial-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .footer-editorial .footer-editorial-brand {
        grid-column: 1 / -1;
    }
}

@media (max-width: 650px) {
    .footer-editorial {
        padding: 3.5rem 0 1.5rem;
    }

    .footer-editorial .footer-editorial-grid {
        grid-template-columns: 1fr;
        gap: 2rem;
    }

    .footer-editorial .footer-editorial-brand {
        grid-column: auto;
    }

    .footer-editorial .footer-editorial-bottom {
        margin-top: 2.5rem;
    }
}

/* SCV_SHARED_FOOTER_END */
'@

$ComponentsContent = [System.IO.File]::ReadAllText($ComponentsPath)

$MarkerPattern = "(?s)" +
    [regex]::Escape($StartMarker) +
    ".*?" +
    [regex]::Escape($EndMarker)

$ComponentsContent = [regex]::Replace(
    $ComponentsContent,
    $MarkerPattern,
    ""
).TrimEnd()

$ComponentsContent = $ComponentsContent +
    "`r`n`r`n" +
    $FooterCss +
    "`r`n"

[System.IO.File]::WriteAllText(
    $ComponentsPath,
    $ComponentsContent,
    $Utf8
)

Write-Host ""
Write-Host "[OK] Footer compartido añadido a components.css." -ForegroundColor Green

# ============================================================
# 3. ORDENAR CSS EN TODAS LAS PLANTILLAS
#    styles.css -> CSS de página -> components.css -> responsive.css
# ============================================================

$TemplateFiles = Get-ChildItem `
    -Path $TemplatesRoot `
    -Recurse `
    -Filter "*.html" `
    -File |
Where-Object {
    $_.DirectoryName -notlike "*\templates\fragments"
}

$CssLinkRegex = [regex]::new(
    '(?is)<link\b[^>]*\bhref="/css/[^"]+\.css"[^>]*>'
)

$UpdatedTemplates = @()
$SkippedTemplates = @()

foreach ($TemplateFile in $TemplateFiles) {
    $Content = [System.IO.File]::ReadAllText($TemplateFile.FullName)
    $Matches = $CssLinkRegex.Matches($Content)

    if ($Matches.Count -lt 4) {
        $SkippedTemplates += $TemplateFile.FullName
        continue
    }

    $Blocks = @()

    foreach ($Match in $Matches) {
        $HrefMatch = [regex]::Match(
            $Match.Value,
            '(?i)\bhref="/css/([^"]+\.css)"'
        )

        if ($HrefMatch.Success) {
            $Blocks += [pscustomobject]@{
                FileName = $HrefMatch.Groups[1].Value
                Html     = $Match.Value.Trim()
                Index    = $Match.Index
                Length   = $Match.Length
            }
        }
    }

    $StylesBlock = $Blocks |
        Where-Object { $_.FileName -eq "styles.css" } |
        Select-Object -First 1

    $ComponentsBlock = $Blocks |
        Where-Object { $_.FileName -eq "components.css" } |
        Select-Object -First 1

    $ResponsiveBlock = $Blocks |
        Where-Object { $_.FileName -eq "responsive.css" } |
        Select-Object -First 1

    $PageBlocks = $Blocks |
        Where-Object {
            $_.FileName -notin @(
                "styles.css",
                "components.css",
                "responsive.css"
            )
        }

    if (
        -not $StylesBlock -or
        -not $ComponentsBlock -or
        -not $ResponsiveBlock -or
        $PageBlocks.Count -lt 1
    ) {
        $SkippedTemplates += $TemplateFile.FullName
        continue
    }

    $OrderedBlocks = @($StylesBlock) +
        @($PageBlocks) +
        @($ComponentsBlock) +
        @($ResponsiveBlock)

    $NewLinks = ($OrderedBlocks.Html -join "`r`n`r`n    ")

    $FirstIndex = ($Blocks | Measure-Object -Property Index -Minimum).Minimum

    $LastBlock = $Blocks |
        Sort-Object Index |
        Select-Object -Last 1

    $EndIndex = $LastBlock.Index + $LastBlock.Length

    $NewContent =
        $Content.Substring(0, $FirstIndex) +
        $NewLinks +
        $Content.Substring($EndIndex)

    if ($NewContent -ne $Content) {
        [System.IO.File]::WriteAllText(
            $TemplateFile.FullName,
            $NewContent,
            $Utf8
        )

        $UpdatedTemplates += $TemplateFile.FullName
    }
}

Write-Host ""
Write-Host "Plantillas con orden CSS actualizado: $($UpdatedTemplates.Count)" -ForegroundColor Green

foreach ($UpdatedTemplate in $UpdatedTemplates) {
    Write-Host "  [OK] $UpdatedTemplate" -ForegroundColor Green
}

if ($SkippedTemplates.Count -gt 0) {
    Write-Host ""
    Write-Host "Plantillas no modificadas automáticamente:" -ForegroundColor Yellow

    foreach ($SkippedTemplate in $SkippedTemplates) {
        Write-Host "  - $SkippedTemplate" -ForegroundColor Yellow
    }
}

# ============================================================
# 4. VERIFICACIÓN
# ============================================================

Write-Host ""
Write-Host "Verificando components.css..." -ForegroundColor Cyan

$FooterChecks = Select-String `
    -Path $ComponentsPath `
    -SimpleMatch `
    -Pattern `
        ".footer-editorial {",
        ".footer-editorial .footer-editorial-grid",
        "SCV_SHARED_FOOTER_END"

if ($FooterChecks.Count -ge 3) {
    Write-Host "[OK] Estilos compartidos del footer presentes." -ForegroundColor Green
}
else {
    throw "No se pudo verificar correctamente el bloque del footer."
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host " COMPONENTES COMPARTIDOS NORMALIZADOS" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ahora ejecuta:" -ForegroundColor Cyan
Write-Host ".\mvnw.cmd clean spring-boot:run"
Write-Host ""
Write-Host "Después recarga cada ruta con Ctrl + F5." -ForegroundColor Cyan
