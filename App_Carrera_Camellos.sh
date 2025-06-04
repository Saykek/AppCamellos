#!/bin/bash

# Ruta base del proyecto
BASE="/Users/saramartinez/Desktop/NuevaCarrera"

# Rutas a los proyectos
SERVIDOR="$BASE/carreracamellos"
CLIENTE="$BASE/clientecarreracamellos"

# Ejecutar el servidor en una nueva terminal
osascript <<EOF
tell application "Terminal"
    do script "cd '$SERVIDOR' && mvn compile exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.servidor.mvc.modelo.Servidor"
end tell
EOF

# Esperar un poco a que el servidor arranque
sleep 2

# Ejecutar cuatro clientes en nuevas terminales
for i in {1..4}
do
osascript <<EOF
tell application "Terminal"
    do script "cd '$CLIENTE' && mvn compile javafx:run"
end tell
EOF
sleep 1
done