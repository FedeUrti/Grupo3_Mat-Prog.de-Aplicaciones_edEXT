#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT_DIR"

mvn clean package

DIST_DIR="$ROOT_DIR/dist"
mkdir -p "$DIST_DIR"
cp "$ROOT_DIR/ServidorWeb_edEXT/target/web.war" "$DIST_DIR/web.war"
cp "$ROOT_DIR/ServidorMobile_edEXT/target/mobile.war" "$DIST_DIR/mobile.war"
cp "$ROOT_DIR/ServidorCentral_edEXT/target/servidor.jar" "$DIST_DIR/servidor.jar"
cp "$ROOT_DIR/scripts/iniciar-servicio.sh" "$DIST_DIR/edext-servicio"
cp "$ROOT_DIR/scripts/abrir-estacion.sh" "$DIST_DIR/edext-estacion"
cp "$ROOT_DIR/scripts/parar-servicio.sh" "$DIST_DIR/edext-parar-servicio"
chmod +x "$DIST_DIR/edext-servicio" "$DIST_DIR/edext-estacion" "$DIST_DIR/edext-parar-servicio"
