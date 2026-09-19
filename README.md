# Lab9-Hilos — Centro Logístico

Simulación gráfica de un centro de distribución de paquetería en Java.
Los paquetes recorren siete etapas (recepción, almacén, clasificación,
empaquetado, expedición, reparto, entregado) atendidas por hilos que
comparten listas enlazadas implementadas desde cero.

## Requisitos

- JDK 17 o superior (probado con JDK 26)
- No usa Maven, Gradle ni librerías externas

## Compilar y ejecutar

En IntelliJ: abrir la carpeta del proyecto y pulsar Run sobre la
configuración `Main`. La carpeta `src` ya está marcada como source root y
la salida va a `out/`.

Desde terminal:

```sh
javac -d out $(find src -name "*.java")
java -cp out paqueteria.Main
```
