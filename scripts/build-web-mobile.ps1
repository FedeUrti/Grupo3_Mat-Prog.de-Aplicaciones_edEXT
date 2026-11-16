param(
    [string]$CentralSoapUrl = $env:EDEXT_SOAP_BASE_URL
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$webPom = Join-Path $root "ServidorWeb_edEXT\pom.xml"
$mobilePom = Join-Path $root "ServidorMobile_edEXT\pom.xml"

Push-Location $root
try {
    & mvn -f $webPom clean package
    if ($LASTEXITCODE -ne 0) {
        throw "El build del Servidor Web termino con codigo $LASTEXITCODE."
    }

    & mvn -f $mobilePom clean package
    if ($LASTEXITCODE -ne 0) {
        throw "El build del Servidor Mobile termino con codigo $LASTEXITCODE."
    }

    $dist = Join-Path $root "dist"
    New-Item -ItemType Directory -Path $dist -Force | Out-Null
    Copy-Item (Join-Path $root "ServidorWeb_edEXT\target\web.war") (Join-Path $dist "web.war") -Force
    Copy-Item (Join-Path $root "ServidorMobile_edEXT\target\mobile.war") (Join-Path $dist "mobile.war") -Force

    if (-not [string]::IsNullOrWhiteSpace($CentralSoapUrl)) {
        $configDir = Join-Path $HOME ".edEXT"
        $configFile = Join-Path $configDir "web.properties"
        New-Item -ItemType Directory -Path $configDir -Force | Out-Null

        $lines = if (Test-Path -LiteralPath $configFile -PathType Leaf) {
            [System.IO.File]::ReadAllLines($configFile)
        } else {
            @()
        }
        $updatedLines = New-Object System.Collections.Generic.List[string]
        $updated = $false
        foreach ($line in $lines) {
            if ($line -match '^\s*central\.soap\.url(?:\s*[=:]|\s+)') {
                if (-not $updated) {
                    $updatedLines.Add("central.soap.url=$CentralSoapUrl")
                }
                $updated = $true
            } else {
                $updatedLines.Add($line)
            }
        }
        if (-not $updated) {
            $updatedLines.Add("central.soap.url=$CentralSoapUrl")
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
        Write-Output "URL SOAP configurada en: $configFile"
    }

    Write-Output "Web compilada: $(Join-Path $dist 'web.war')"
    Write-Output "Mobile compilado: $(Join-Path $dist 'mobile.war')"
}
finally {
    Pop-Location
}
