# Listas, Pilas y Colas en Java — Implementación y análisis de complejidad

Estructuras de Datos 2026-2 · Universidad Nacional de Colombia
Profesor: David Herrera · Monitora: Ángela Camila Siabato Londoño
estudiante: Raúl Santiago Bermúdez Camacho

Implementación manual de:

| Estructura | Clase |
|---|---|
| Lista simplemente enlazada sin cola | `edu.unal.ed.list.SinglyLinkedListNoTail` |
| Lista simplemente enlazada con cola | `edu.unal.ed.list.SinglyLinkedListWithTail` |
| Lista doblemente enlazada sin cola | `edu.unal.ed.list.DoublyLinkedListNoTail` |
| Lista doblemente enlazada con cola | `edu.unal.ed.list.DoublyLinkedListWithTail` |
| Pila sobre arreglo dinámico circular | `edu.unal.ed.stack.ArrayStack` (interfaz `MyStack<T>`) |
| Cola sobre arreglo dinámico circular | `edu.unal.ed.queue.ArrayQueue` (interfaz `MyQueue<T>`) |

Pila y cola usan el arreglo circular propio `edu.unal.ed.circular.CircularArray`
(capacidad inicial 16, se duplica al llenarse).

## Estructura del repositorio

```
├── src/main/java/
│   ├── Main.java                   Mediciones (adaptado del Main.java del profesor)
│   ├── Operation.java              Interfaz funcional del Main.java del profesor
│   └── edu/unal/ed/
│       ├── list/                   MyList<T>, ListNode<T>, nodos y las 4 listas
│       ├── circular/               CircularArray<T> (arreglo dinámico circular)
│       ├── stack/                  MyStack<T>, ArrayStack<T>
│       ├── queue/                  MyQueue<T>, ArrayQueue<T>
│       └── benchmark/              Medición con System.nanoTime() y escritura de CSV
├── results/
│   ├── tiempos.csv                 Resultados medidos
│   ├── entorno.txt                 Equipo, versión de Java y parámetros de la corrida
│   ├── graficar.py                 Genera las gráficas a partir de los CSV
│   └── graficas/                   Gráficas (PNG)
├── informe/                        Informe en PDF
└── ejecutar_mediciones.bat         Ejecuta las mediciones en Windows
```

## Compilar

Requiere Java 17 o superior.

```bash
javac -d bin $(find src/main -name '*.java')          # Linux / macOS
```

En Windows (PowerShell):

```powershell
javac -d bin (Get-ChildItem -Recurse src\main -Filter *.java).FullName
```

## Medir los tiempos

Tamaños de entrada: 10, 100, 10^4, 10^6 y 10^7. La medición y la graficación están
separadas: Java solo escribe CSV.

En Windows basta con hacer doble clic en `ejecutar_mediciones.bat`. Equivale a:

```bash
java -Xmx3g -XX:+UseParallelGC -cp bin Main
```

## Generar las gráficas

```bash
pip install matplotlib
python results/graficar.py
```
