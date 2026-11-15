#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
DEFAULT_JAR="$SCRIPT_DIR/servidor.jar"
if [ ! -f "$DEFAULT_JAR" ]; then
    DEFAULT_JAR="$ROOT_DIR/dist/servidor.jar"
fi
JAR_PATH=${1:-${EDEXT_JAR:-"$DEFAULT_JAR"}}

if [ ! -f "$JAR_PATH" ]; then
    echo "No se encontró el JAR de la Estación: $JAR_PATH" >&2
    exit 1
fi

exec java -jar "$JAR_PATH" --estacion
