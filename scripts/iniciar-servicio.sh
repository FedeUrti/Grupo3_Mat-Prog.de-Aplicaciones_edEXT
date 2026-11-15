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
    echo "No se encontró el JAR del servidor: $JAR_PATH" >&2
    exit 1
fi

JAR_DIR=$(CDPATH= cd -- "$(dirname -- "$JAR_PATH")" && pwd)
JAR_PATH="$JAR_DIR/$(basename -- "$JAR_PATH")"
CONFIG_DIR=${HOME:?No está definida la variable HOME}/.edEXT
PID_FILE="$CONFIG_DIR/servidor.pid"
LOG_FILE="$CONFIG_DIR/servidor.log"

mkdir -p "$CONFIG_DIR"

service_command_matches() {
    process_id=$1
    jar_path=$2
    process_command=$(ps -p "$process_id" -o args= 2>/dev/null || true)
    case "$process_command" in
        *"java -jar $jar_path"*) return 0 ;;
        *) return 1 ;;
    esac
}

if [ -f "$PID_FILE" ]; then
    existing_pid=$(sed -n '1p' "$PID_FILE")
    existing_jar=$(sed -n '2p' "$PID_FILE")
    if [ -n "$existing_pid" ] && kill -0 "$existing_pid" 2>/dev/null; then
        if [ -z "$existing_jar" ]; then
            existing_jar="$JAR_PATH"
        fi
        if service_command_matches "$existing_pid" "$existing_jar"; then
            echo "El servidor ya está en ejecución (PID $existing_pid). Log: $LOG_FILE"
            exit 0
        fi
        echo "El PID $existing_pid del archivo $PID_FILE pertenece a otro proceso; no se iniciará otro servidor." >&2
        exit 1
    fi
    rm -f "$PID_FILE"
fi

if ! command -v java >/dev/null 2>&1; then
    echo "No se encontró java en PATH." >&2
    exit 1
fi

nohup java -jar "$JAR_PATH" >"$LOG_FILE" 2>&1 </dev/null &
SERVER_PID=$!
printf '%s\n%s\n' "$SERVER_PID" "$JAR_PATH" > "$PID_FILE"

sleep 1
if ! kill -0 "$SERVER_PID" 2>/dev/null || ! service_command_matches "$SERVER_PID" "$JAR_PATH"; then
    echo "El servidor no pudo mantenerse iniciado. Últimas líneas del log:" >&2
    tail -n 40 "$LOG_FILE" >&2
    rm -f "$PID_FILE"
    exit 1
fi

echo "Servidor iniciado en segundo plano (PID $SERVER_PID)."
echo "Log: $LOG_FILE"
STOP_SCRIPT="$SCRIPT_DIR/edext-parar-servicio"
if [ ! -x "$STOP_SCRIPT" ]; then
    STOP_SCRIPT="$ROOT_DIR/scripts/parar-servicio.sh"
fi
echo "Detenerlo con: $STOP_SCRIPT"
