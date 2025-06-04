#!/bin/bash

# Ruta base del proyecto: directorio actual donde ejecutes el script
BASE="$(pwd)"

SERVIDOR="$BASE/carreracamellos"
CLIENTE="$BASE/clientecarreracamellos"

# Función para abrir una terminal y ejecutar comando según SO
abrir_terminal() {
  local cmd="$1"

  if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS - usar osascript con Terminal.app
    osascript <<EOF
tell application "Terminal"
    do script "$cmd"
    activate
end tell
EOF
  elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux - intentar con gnome-terminal, xterm o konsole
    if command -v gnome-terminal &> /dev/null; then
      gnome-terminal -- bash -c "$cmd; exec bash"
    elif command -v xterm &> /dev/null; then
      xterm -e "$cmd; bash"
    elif command -v konsole &> /dev/null; then
      konsole -e bash -c "$cmd; exec bash"
    else
      echo "No se encontró un emulador de terminal compatible."
      echo "Ejecuta manualmente: $cmd"
    fi
  else
    echo "Sistema operativo no soportado para abrir terminales automáticamente."
    echo "Ejecuta manualmente: $cmd"
  fi
}

# Lanzar servidor
abrir_terminal "cd '$SERVIDOR' && mvn compile exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.servidor.mvc.modelo.Servidor"

sleep 2

# Lanzar 4 clientes
for i in {1..4}; do
  abrir_terminal "cd '$CLIENTE' && mvn compile javafx:run"
  sleep 1
done