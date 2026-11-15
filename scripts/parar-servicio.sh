#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
CONFIG_DIR=${HOME:?No está definida la variable HOME}/.edEXT
PID_FILE="$CONFIG_DIR/servidor.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "No hay un archivo PID del servidor; no parece estar iniciado."
    exit 0
fi

SERVER_PID=$(sed -n '1p' "$PID_FILE")
JAR_PATH=$(sed -n '2p' "$PID_FILE")

case "$SERVER_PID" in
    ''|*[!0-9]*)
        echo "El archivo PID no contiene un PID válido: $PID_FILE" >&2
        exit 1
        ;;
esac

if [ -z "$JAR_PATH" ]; then
    DEFAULT_JAR="$SCRIPT_DIR/servidor.jar"
    if [ ! -f "$DEFAULT_JAR" ]; then
        DEFAULT_JAR="$ROOT_DIR/dist/servidor.jar"
    fi
    JAR_PATH=${1:-${EDEXT_JAR:-"$DEFAULT_JAR"}}
    JAR_DIR=$(CDPATH= cd -- "$(dirname -- "$JAR_PATH")" && pwd)
    JAR_PATH="$JAR_DIR/$(basename -- "$JAR_PATH")"
fi

process_command_matches() {
    process_command=$(ps -p "$SERVER_PID" -o args= 2>/dev/null || true)
    case "$process_command" in
        *"java -jar $JAR_PATH"*) return 0 ;;
        *) return 1 ;;
    esac
}

if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "El proceso ya no está activo; limpiando el PID obsoleto."
    rm -f "$PID_FILE"
    exit 0
fi

if ! process_command_matches; then
    echo "El PID $SERVER_PID pertenece a otro proceso; no se detendrá." >&2
    exit 1
fi

kill "$SERVER_PID"
attempt=0
while kill -0 "$SERVER_PID" 2>/dev/null && process_command_matches; do
    if [ "$attempt" -ge 10 ]; then
        echo "El servidor no terminó después de 10 segundos; revise el proceso $SERVER_PID." >&2
        exit 1
    fi
    sleep 1
    attempt=$((attempt + 1))
done

rm -f "$PID_FILE"
echo "Servidor detenido."
