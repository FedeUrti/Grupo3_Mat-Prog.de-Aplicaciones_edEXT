param(
    [string]$JarPath
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

if ([string]::IsNullOrWhiteSpace($JarPath)) {
    $JarPath = $env:EDEXT_JAR
}
if ([string]::IsNullOrWhiteSpace($JarPath)) {
    $JarPath = Join-Path $root "dist\servidor.jar"
}

$JarPath = [System.IO.Path]::GetFullPath($JarPath)
if (-not (Test-Path -LiteralPath $JarPath -PathType Leaf)) {
    throw "No se encontró el JAR de la Estación: $JarPath"
}

& java -jar $JarPath --estacion
exit $LASTEXITCODE
