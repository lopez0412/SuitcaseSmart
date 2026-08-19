# check-status.ps1 — Estado actual del proyecto SuitcaseSmart
# Uso: .\scripts\check-status.ps1

$root = Split-Path $PSScriptRoot -Parent
$gradlePath = Join-Path $root "app\build.gradle.kts"
$aabPath    = Join-Path $root "app\release\suitcaseSmart-release.aab"
$sep        = "-" * 56

Write-Host ""
Write-Host "========================================================"
Write-Host "  SuitcaseSmart — Estado del Proyecto"
Write-Host "========================================================"

# ── Version ──────────────────────────────────────────────────
Write-Host ""
Write-Host "VERSION"
Write-Host $sep
$versionName = (Select-String -Path $gradlePath -Pattern 'versionName\s*=\s*"([^"]+)"').Matches[0].Groups[1].Value
$versionCode = (Select-String -Path $gradlePath -Pattern 'versionCode\s*=\s*(\d+)').Matches[0].Groups[1].Value
Write-Host "  versionName : $versionName"
Write-Host "  versionCode : $versionCode"

# ── SDK targets ───────────────────────────────────────────────
$compileSdk = (Select-String -Path $gradlePath -Pattern 'compileSdk\s*=\s*(\d+)').Matches[0].Groups[1].Value
$targetSdk  = (Select-String -Path $gradlePath -Pattern 'targetSdk\s*=\s*(\d+)').Matches[0].Groups[1].Value
$minSdk     = (Select-String -Path $gradlePath -Pattern 'minSdk\s*=\s*(\d+)').Matches[0].Groups[1].Value
Write-Host "  compileSdk  : $compileSdk  |  targetSdk: $targetSdk  |  minSdk: $minSdk"

# ── AAB ───────────────────────────────────────────────────────
Write-Host ""
Write-Host "RELEASE AAB"
Write-Host $sep
if (Test-Path $aabPath) {
    $aabInfo = Get-Item $aabPath
    $sizeMB  = [math]::Round($aabInfo.Length / 1MB, 2)
    Write-Host "  [OK] Existe: $aabPath"
    Write-Host "       Tamanio : ${sizeMB} MB"
    Write-Host "       Fecha   : $($aabInfo.LastWriteTime.ToString('yyyy-MM-dd HH:mm'))"
} else {
    Write-Host "  [--] No existe. Ejecuta: ./gradlew bundleRelease"
}

# ── Git ───────────────────────────────────────────────────────
Write-Host ""
Write-Host "GIT"
Write-Host $sep
Push-Location $root
$branch      = git rev-parse --abbrev-ref HEAD 2>$null
$lastCommit  = git log -1 --pretty=format:"%h %s (%cr)" 2>$null
$statusLines = git status --short 2>$null
Pop-Location

Write-Host "  Rama actual : $branch"
Write-Host "  Ultimo commit: $lastCommit"

if ($statusLines) {
    Write-Host "  Cambios sin commitear:"
    $statusLines | ForEach-Object { Write-Host "    $_" }
} else {
    Write-Host "  Working tree limpio"
}

# ── ROADMAP (proximas versiones) ─────────────────────────────
Write-Host ""
Write-Host "ROADMAP"
Write-Host $sep
$roadmapPath = Join-Path $root "ROADMAP.md"
if (Test-Path $roadmapPath) {
    $lines = Get-Content $roadmapPath
    $versionHeaders = $lines | Where-Object { $_ -match "^## v\d" }
    $versionHeaders | ForEach-Object { Write-Host "  $_" }
} else {
    Write-Host "  ROADMAP.md no encontrado"
}

# ── TODOs en codigo ──────────────────────────────────────────
Write-Host ""
Write-Host "TODOs EN CODIGO"
Write-Host $sep
$srcPath = Join-Path $root "app\src\main\java"
$todos   = Get-ChildItem -Path $srcPath -Recurse -Filter "*.kt" |
           Select-String -Pattern "//\s*TODO" |
           Select-Object Filename, LineNumber, Line

if ($todos.Count -gt 0) {
    $todos | ForEach-Object {
        Write-Host "  $($_.Filename):$($_.LineNumber) → $($_.Line.Trim())"
    }
} else {
    Write-Host "  Sin TODOs en el codigo Kotlin"
}

# ── Dependencias desactualizadas (aviso manual) ──────────────
Write-Host ""
Write-Host "DEPENDENCIAS CLAVE"
Write-Host $sep
Write-Host "  Firebase BOM     : 34.11.0"
Write-Host "  Compose BOM      : 2026.03.00"
Write-Host "  Navigation       : 2.8.8"
Write-Host "  Coil             : 3.4.0"
Write-Host "  (verificar manualmente en libs.versions.toml o build.gradle.kts)"

Write-Host ""
Write-Host "========================================================"
Write-Host ""
