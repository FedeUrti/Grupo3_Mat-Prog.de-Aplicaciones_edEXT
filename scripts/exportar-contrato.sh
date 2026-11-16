#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
output_dir="${2:-$script_dir/../ServidorWeb_edEXT/src/main/wsdl}"
if [[ $# -ge 1 ]]; then
    central_base_url="$1"
elif [[ -n "${EDEXT_SOAP_BIND_ADDRESS:-}" ]]; then
    central_base_url="$EDEXT_SOAP_BIND_ADDRESS"
else
    config_file="${HOME}/.edEXT/servidor.properties"
    if [[ ! -r "$config_file" ]]; then
        echo "Indicá la URL del central o configurá EDEXT_SOAP_BIND_ADDRESS / ~/.edEXT/servidor.properties." >&2
        exit 1
    fi
    central_base_url="$(sed -n 's/^[[:space:]]*soap\.bind\.url[[:space:]]*=[[:space:]]*//p' "$config_file" | tail -n 1 | tr -d '\r')"
    if [[ -z "$central_base_url" ]]; then
        echo "No se encontró soap.bind.url en $config_file." >&2
        exit 1
    fi
fi
mkdir -p "$output_dir"

services=(categoria curso edicion instituto programa usuario)
for service in "${services[@]}"; do
    service_url="${central_base_url%/}/$service"
    wsdl_url="$service_url?wsdl"
    xsd_url="$service_url?xsd=1"
    wsdl_path="$output_dir/$service.wsdl"
    xsd_path="$output_dir/$service.xsd"

    curl --fail --silent --show-error --location "$wsdl_url" \
        | sed "s|$service_url?xsd=1|$service.xsd|g" > "$wsdl_path"
    curl --fail --silent --show-error --location "$xsd_url" > "$xsd_path"

    test -s "$wsdl_path" && test -s "$xsd_path" || {
        echo "El contrato de '$service' está vacío; verificá el servidor central." >&2
        exit 1
    }
    echo "Exportado $service.wsdl y $service.xsd"
done

echo "Contrato exportado en $output_dir"
