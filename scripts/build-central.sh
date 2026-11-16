#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)

mvn -f "$ROOT_DIR/ServidorCentral_edEXT/pom.xml" clean package

DIST_DIR="$ROOT_DIR/dist"
mkdir -p "$DIST_DIR"
cp "$ROOT_DIR/ServidorCentral_edEXT/target/servidor.jar" "$DIST_DIR/servidor.jar"

CONFIG_DIR=${HOME:?No está definida la variable HOME}/.edEXT
CONFIG_FILE="$CONFIG_DIR/servidor.properties"
mkdir -p "$CONFIG_DIR"
TEMP_FILE=$(mktemp "$CONFIG_FILE.XXXXXX")
trap 'rm -f "$TEMP_FILE"' EXIT HUP INT TERM

if [ -f "$CONFIG_FILE" ]; then
    awk '
        BEGIN { updated = 0 }
        /^[[:space:]]*soap\.bind\.url([[:space:]]*[:=]|[[:space:]]+)/ {
            if (!updated) print "soap.bind.url=http://0.0.0.0:9128/edext/soap"
            updated = 1
            next
        }
        { print }
        END {
            if (!updated) print "soap.bind.url=http://0.0.0.0:9128/edext/soap"
        }
    ' "$CONFIG_FILE" > "$TEMP_FILE"
else
    printf '%s\n' 'soap.bind.url=http://0.0.0.0:9128/edext/soap' > "$TEMP_FILE"
fi

mv "$TEMP_FILE" "$CONFIG_FILE"
trap - EXIT HUP INT TERM

printf 'Servidor Central compilado: %s\n' "$DIST_DIR/servidor.jar"
printf 'SOAP escuchará en todas las interfaces: %s\n' "$CONFIG_FILE"
