param()

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$pom = Join-Path $root "ServidorCentral_edEXT\pom.xml"

Push-Location $root
try {
    & mvn -f $pom clean package
    if ($LASTEXITCODE -ne 0) {
        throw "El build del Servidor Central termino con codigo $LASTEXITCODE."
    }

    $dist = Join-Path $root "dist"
    New-Item -ItemType Directory -Path $dist -Force | Out-Null
    Copy-Item (Join-Path $root "ServidorCentral_edEXT\target\servidor.jar") (Join-Path $dist "servidor.jar") -Force

    $configDir = Join-Path $HOME ".edEXT"
    $configFile = Join-Path $configDir "servidor.properties"
    New-Item -ItemType Directory -Path $configDir -Force | Out-Null

    $lines = if (Test-Path -LiteralPath $configFile -PathType Leaf) {
        [System.IO.File]::ReadAllLines($configFile)
    } else {
        @()
    }
    $updatedLines = New-Object System.Collections.Generic.List[string]
    $updated = $false
    foreach ($line in $lines) {
        if ($line -match '^\s*soap\.bind\.url(?:\s*[=:]|\s+)') {
            if (-not $updated) {
                $updatedLines.Add("soap.bind.url=http://0.0.0.0:9128/edext/soap")
            }
            $updated = $true
        } else {
            $updatedLines.Add($line)
        }
    }
    if (-not $updated) {
        $updatedLines.Add("soap.bind.url=http://0.0.0.0:9128/edext/soap")
    }

    $tempFile = Join-Path $configDir ([System.IO.Path]::GetRandomFileName())
    try {
        [System.IO.File]::WriteAllLines(
            $tempFile,
            $updatedLines.ToArray(),
            [System.Text.UTF8Encoding]::new($false))
        Move-Item -LiteralPath $tempFile -Destination $configFile -Force
    } finally {
        if (Test-Path -LiteralPath $tempFile) {
            Remove-Item -LiteralPath $tempFile
        }
    }

    Write-Output "Servidor Central compilado: $(Join-Path $dist 'servidor.jar')"
    Write-Output "SOAP escuchará en todas las interfaces: $configFile"
}
finally {
    Pop-Location
}
