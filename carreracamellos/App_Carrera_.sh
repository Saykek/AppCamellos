#!/bin/bash

# Limpiar el proyecto
echo "Limpiando el proyecto..."
mvn clean

# Compilar el proyecto
echo "Compilando el proyecto..."
mvn compile

# Ejecutar el servidor
echo "Ejecutando el servidor..."
mvn exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.servidor.mvc.modelo.Servidor &

# Esperar un poco para asegurar que el servidor haya iniciado
sleep 2

# Ejecutar los clientes
echo "Ejecutando cliente 1..."
mvn exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.cliente.Cliente1 &

echo "Ejecutando cliente 2..."
mvn exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.cliente.Cliente2 &

echo "Ejecutando cliente 3..."
mvn exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.cliente.Cliente3 &

echo "Ejecutando cliente 4..."
mvn exec:java -Dexec.mainClass=es.etg.smr.carreracamellos.cliente.Cliente4 &

# Esperar a que todos los procesos terminen (esto es opcional)
wait

