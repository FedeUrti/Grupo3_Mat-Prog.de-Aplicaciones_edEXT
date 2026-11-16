#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
CENTRAL_SOAP_URL=${1:-${EDEXT_SOAP_BASE_URL:-}}

mvn -f "$ROOT_DIR/ServidorWeb_edEXT/pom.xml" clean package
mvn -f "$ROOT_DIR/ServidorMobile_edEXT/pom.xml" clean package

DIST_DIR="$ROOT_DIR/dist"
mkdir -p "$DIST_DIR"
cp "$ROOT_DIR/ServidorWeb_edEXT/target/web.war" "$DIST_DIR/web.war"
cp "$ROOT_DIR/ServidorMobile_edEXT/target/mobile.war" "$DIST_DIR/mobile.war"

if [ -n "$CENTRAL_SOAP_URL" ]; then
    CONFIG_DIR=${HOME:?No está definida la variable HOME}/.edEXT
    CONFIG_FILE="$CONFIG_DIR/web.properties"
    mkdir -p "$CONFIG_DIR"
    TEMP_FILE=$(mktemp "$CONFIG_FILE.XXXXXX")
    trap 'rm -f "$TEMP_FILE"' EXIT HUP INT TERM

    if [ -f "$CONFIG_FILE" ]; then
        awk -v url="$CENTRAL_SOAP_URL" '
            BEGIN { updated = 0 }
            /^[[:space:]]*central\.soap\.url([[:space:]]*[:=]|[[:space:]]+)/ {
                if (!updated) print "central.soap.url=" url
                updated = 1
                next
            }
            { print }
            END {
                if (!updated) print "central.soap.url=" url
            }
        ' "$CONFIG_FILE" > "$TEMP_FILE"
    else
        printf 'central.soap.url=%s\n' "$CENTRAL_SOAP_URL" > "$TEMP_FILE"
    fi

    mv "$TEMP_FILE" "$CONFIG_FILE"
    trap - EXIT HUP INT TERM
    printf 'URL SOAP configurada en: %s\n' "$CONFIG_FILE"
fi

printf 'Web compilada: %s\n' "$DIST_DIR/web.war"
printf 'Mobile compilado: %s\n' "$DIST_DIR/mobile.war"
