$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    & mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "mvn clean package finalizó con código $LASTEXITCODE."
    }

    $dist = Join-Path $root "dist"
    New-Item -ItemType Directory -Path $dist -Force | Out-Null
    Copy-Item (Join-Path $root "ServidorWeb_edEXT\target\web.war") (Join-Path $dist "web.war") -Force
    Copy-Item (Join-Path $root "ServidorMobile_edEXT\target\mobile.war") (Join-Path $dist "mobile.war") -Force
    Copy-Item (Join-Path $root "ServidorCentral_edEXT\target\servidor.jar") (Join-Path $dist "servidor.jar") -Force
    Copy-Item (Join-Path $root "scripts\iniciar-servicio.sh") (Join-Path $dist "edext-servicio") -Force
    Copy-Item (Join-Path $root "scripts\abrir-estacion.sh") (Join-Path $dist "edext-estacion") -Force
    Copy-Item (Join-Path $root "scripts\parar-servicio.sh") (Join-Path $dist "edext-parar-servicio") -Force
}
finally {
    Pop-Location
}
