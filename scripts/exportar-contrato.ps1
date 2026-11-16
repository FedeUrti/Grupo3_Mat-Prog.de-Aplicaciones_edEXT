param(
    [string]$CentralBaseUrl = "",
    [string]$OutputDirectory = (Join-Path $PSScriptRoot "..\ServidorWeb_edEXT\src\main\wsdl")
)

$ErrorActionPreference = "Stop"
if ([string]::IsNullOrWhiteSpace($CentralBaseUrl)) {
    $CentralBaseUrl = $env:EDEXT_SOAP_BIND_ADDRESS
}
if ([string]::IsNullOrWhiteSpace($CentralBaseUrl)) {
    $configPath = Join-Path $HOME ".edEXT\servidor.properties"
    if (-not (Test-Path -LiteralPath $configPath -PathType Leaf)) {
        throw "Indicá la URL del central o configurá EDEXT_SOAP_BIND_ADDRESS / $configPath."
    }
    $property = Get-Content -LiteralPath $configPath -Encoding UTF8 |
        Where-Object { $_ -match '^\s*soap\.bind\.url\s*=' } |
        Select-Object -Last 1
    if ($null -eq $property) {
        throw "No se encontró soap.bind.url en $configPath."
    }
    $CentralBaseUrl = ($property -replace '^\s*soap\.bind\.url\s*=\s*', '').Trim()
}
$CentralBaseUrl = $CentralBaseUrl.TrimEnd("/")
$OutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
$services = @("categoria", "curso", "edicion", "instituto", "programa", "usuario")
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)

New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

foreach ($service in $services) {
    $serviceUrl = "$CentralBaseUrl/$service"
    try {
        $wsdlResponse = Invoke-WebRequest -Uri "$serviceUrl`?wsdl" -UseBasicParsing
        $xsdResponse = Invoke-WebRequest -Uri "$serviceUrl`?xsd=1" -UseBasicParsing
    } catch {
        throw "No se pudo exportar el contrato de '$service' desde $serviceUrl. Verificá que el servidor central esté activo. Detalle: $($_.Exception.Message)"
    }

    if ($wsdlResponse.StatusCode -ne 200 -or $xsdResponse.StatusCode -ne 200) {
        throw "El central no devolvió HTTP 200 para el contrato de '$service'."
    }

    $wsdl = $wsdlResponse.Content.Replace("$serviceUrl`?xsd=1", "$service.xsd")
    try {
        [void][xml]$wsdl
        [void][xml]$xsdResponse.Content
    } catch {
        throw "El central devolvió XML inválido para '$service': $($_.Exception.Message)"
    }

    [System.IO.File]::WriteAllText(
        (Join-Path $OutputDirectory "$service.wsdl"),
        $wsdl,
        $utf8WithoutBom
    )
    [System.IO.File]::WriteAllText(
        (Join-Path $OutputDirectory "$service.xsd"),
        $xsdResponse.Content,
        $utf8WithoutBom
    )
    Write-Output "Exportado $service.wsdl y $service.xsd"
}

Write-Output "Contrato exportado en $OutputDirectory"
