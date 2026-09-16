# Quadratic Assignment Problem (QAP) Optimization - Ford Valencia

Este repositorio contiene la implementación, análisis y comparación de distintas metaheurísticas para resolver el Problema de Asignación Cuadrática (QAP) aplicado a la optimización del flujo de piezas y la distribución de departamentos en la planta de montaje de Ford Valencia.

> Caso de uso: Minimizar el coste total de transporte interno $min \sum F_{ij} \cdot D_{S(i)S(j)}$ donde $F$ es la matriz de flujos entre departamentos y $D$ la matriz de distancias entre localizaciones.

---

## Enfoques e Implementaciones

El problema se aborda de forma incremental mediante tres arquitecturas algorítmicas implementadas en **Java**:

1. `practica02/` (Algoritmos Genéticos Generacionales):
   - Población estocástica, selección por torneo, operador de cruce `OX2` y mutación por intercambio `2-opt`.
2. `practica03/` (Algoritmo Memético Híbrido):
   - Combinación de evolución poblacional con disparadores de Búsqueda Tabú aplicados periódicamente sobre el individuo élite.
   - Elitismo absoluto con reinserción explícita.

---

## Comparativa de Rendimiento

Resumen del rendimiento promedio sobre las instancias reales de prueba (`FORD01` a `FORD04`):

| Algoritmo | Desviación Promedio del Óptimo (%) | Tiempo Promedio de Ejecución (s) | Característica Principal |
| :--- | :---: | :---: | :--- |
| **Algoritmo Memético (MEM)** | **0.04%** | **~0.13s** | **Mejor equilibrio:** Alta calidad de solución con un tiempo casi instantáneo. |
| **Genético Generacional (EVOL)** | 0.09% | ~0.86s | Alta exploración estocástica, mayor variabilidad. |

---

## Estructura del Proyecto

Cada práctica (`practica02/` y `practica03/`) contiene:
* `src/`: Código fuente en Java.
* `archivos/`: Instancias de pruebas (`FORD01`-`FORD04`) y fichero `configurador.txt`.
* `Memoria.pdf`: Análisis exhaustivo con el diseño algorítmico, pruebas empíricas y estudio comparativo.

---

## Cómo ejecutar las prácticas

Dentro de la carpeta de cada método existe una carpeta llamada archivos. El archivo `configurador.txt` cuenta con la estructura para ejecutar distintas instancias del problema.
