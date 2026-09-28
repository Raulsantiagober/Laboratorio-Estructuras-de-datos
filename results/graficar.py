"""
Genera las gráficas del laboratorio a partir de results/tiempos.csv.

Se ejecuta DESPUÉS del programa Java y por separado, de modo que ninguna operación de
graficación ocurre dentro de una sección cronometrada.

Uso:  python3 results/graficar.py            (desde la raíz del repositorio)
Requiere: matplotlib  (pip install matplotlib)

Salida:
  results/graficas/*.png   gráficas (tiempo vs n, escala log-log, microsegundos)
"""

import csv
import math
import os
from collections import defaultdict

import matplotlib

matplotlib.use("Agg")  # sin ventana: solo archivos
import matplotlib.pyplot as plt  # noqa: E402
from matplotlib.ticker import FuncFormatter, LogLocator, NullFormatter  # noqa: E402

AQUI = os.path.dirname(os.path.abspath(__file__))
CSV = os.path.join(AQUI, "tiempos.csv")
SALIDA = os.path.join(AQUI, "graficas")

# Medida graficada: media recortada al 10 % (un promedio robusto a pausas aisladas del GC).
MEDIDA = "media_recortada_ns"

# Paleta categórica validada, en orden fijo (azul, naranja, aqua, amarillo, magenta, verde,
# violeta, rojo). Cada implementación conserva siempre el mismo color en todas las gráficas.
PALETA = ["#2a78d6", "#eb6834", "#1baf7a", "#eda100", "#e87ba4", "#008300", "#4a3aa7", "#e34948"]
MARCADORES = ["o", "s", "^", "D", "v", "P", "X", "*"]

LISTAS = [
    "SinglyLinkedListNoTail",
    "SinglyLinkedListWithTail",
    "DoublyLinkedListNoTail",
    "DoublyLinkedListWithTail",
]
METODOS_LISTA = ["PushFront", "PushBack", "PopFront", "PopBack", "Find", "Erase",
                 "AddBefore", "AddAfter", "TopFront", "TopBack", "Empty", "Size"]
METODOS_PILA = ["push", "pop", "peek", "isEmpty", "size", "delete"]
METODOS_COLA = ["enqueue", "dequeue", "front", "isEmpty", "size", "delete"]

# Parte 3: (título, (estructura_lista, método_lista), (estructura_arreglo, método_arreglo))
PARES_PILA = [
    ("PushFront vs push", ("SinglyLinkedListNoTail", "PushFront"), ("ArrayStack", "push")),
    ("PopFront vs pop", ("SinglyLinkedListNoTail", "PopFront"), ("ArrayStack", "pop")),
    ("TopFront vs peek", ("SinglyLinkedListNoTail", "TopFront"), ("ArrayStack", "peek")),
    ("Empty vs isEmpty", ("SinglyLinkedListNoTail", "Empty"), ("ArrayStack", "isEmpty")),
    ("Size vs size", ("SinglyLinkedListNoTail", "Size"), ("ArrayStack", "size")),
    ("Find+Erase vs delete", ("DoublyLinkedListNoTail", "FindErase"), ("ArrayStack", "delete")),
]
PARES_COLA = [
    ("PushBack vs enqueue", ("SinglyLinkedListWithTail", "PushBack"), ("ArrayQueue", "enqueue")),
    ("PopFront vs dequeue", ("SinglyLinkedListWithTail", "PopFront"), ("ArrayQueue", "dequeue")),
    ("TopFront vs front", ("SinglyLinkedListWithTail", "TopFront"), ("ArrayQueue", "front")),
    ("Empty vs isEmpty", ("SinglyLinkedListWithTail", "Empty"), ("ArrayQueue", "isEmpty")),
    ("Size vs size", ("SinglyLinkedListWithTail", "Size"), ("ArrayQueue", "size")),
    ("Find+Erase vs delete", ("DoublyLinkedListWithTail", "FindErase"), ("ArrayQueue", "delete")),
]

TINTA = "#0b0b0b"
TINTA_2 = "#52514e"
REJILLA = "#e4e3df"


def cargar():
    """datos[(estructura, metodo)] = [(n, microsegundos), ...] ordenado por n.

    Lee tiempos.csv y, si existe, tiempos_desde_<n>.csv (corrida aparte de 10^8)."""
    archivos = [CSV] + sorted(
        os.path.join(AQUI, a) for a in os.listdir(AQUI)
        if a.startswith("tiempos_desde_") and a.endswith(".csv"))
    datos = defaultdict(list)
    for archivo in archivos:
        with open(archivo, newline="", encoding="utf-8") as f:
            for fila in csv.DictReader(f):
                datos[(fila["estructura"], fila["metodo"])].append(
                    (int(fila["n"]), float(fila[MEDIDA]) / 1000.0))
    for serie in datos.values():
        serie.sort()
    return datos


def estilo_ejes(ax, titulo, etiquetas=True):
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_title(titulo, fontsize=11, color=TINTA, loc="left", fontweight="bold")
    if etiquetas:
        ax.set_xlabel("Tamaño n (escala log)", fontsize=9, color=TINTA_2)
        ax.set_ylabel("Tiempo por operación (µs, escala log)", fontsize=9, color=TINTA_2)
    ax.yaxis.set_major_locator(LogLocator(base=10))
    ax.yaxis.set_major_formatter(FuncFormatter(etiqueta_us))
    ax.yaxis.set_minor_formatter(NullFormatter())
    ax.grid(True, which="major", color=REJILLA, linewidth=0.8)
    ax.grid(False, which="minor")
    ax.tick_params(colors=TINTA_2, labelsize=8)
    for lado in ("top", "right"):
        ax.spines[lado].set_visible(False)
    for lado in ("left", "bottom"):
        ax.spines[lado].set_color("#b9b8b3")


def etiqueta_us(v, _pos):
    """Etiquetas decimales legibles para el eje y (µs): 0.01, 0.1, 1, 10, 1 000 ..."""
    if v >= 1:
        return f"{v:,.0f}".replace(",", " ")
    return f"{v:.{max(0, -int(math.floor(math.log10(v))))}f}"


def rango_minimo(ax, decadas=2.0):
    """Si los datos abarcan menos de `decadas` órdenes de magnitud, amplía el eje y alrededor
    de ellos. Evita que una gráfica de métodos O(1) haga zoom sobre variaciones de pocos
    nanosegundos y parezca mostrar tendencias que son solo ruido."""
    ys = [y for linea in ax.get_lines() for y in linea.get_ydata()]
    if not ys:
        return
    lo, hi = min(ys), max(ys)
    if math.log10(hi / lo) < decadas:
        centro = math.sqrt(lo * hi)
        mitad = 10 ** (decadas / 2)
        ax.set_ylim(centro / mitad, centro * mitad)


def serie(ax, datos, clave, etiqueta, i):
    puntos = datos.get(clave)
    if not puntos:
        return
    xs = [p[0] for p in puntos]
    # Un promedio de 0 significa "por debajo de la resolución del reloj"; en escala log se
    # dibuja en 0.005 µs (5 ns) para que el punto siga visible.
    ys = [max(p[1], 0.005) for p in puntos]
    ax.plot(xs, ys, color=PALETA[i], marker=MARCADORES[i], markersize=5, linewidth=2,
            label=etiqueta)


def guardar(fig, nombre):
    for ax in fig.get_axes():
        rango_minimo(ax)
    fig.savefig(os.path.join(SALIDA, nombre), dpi=150, bbox_inches="tight", facecolor="white")
    plt.close(fig)


def parte1(datos):
    # Una gráfica por método comparando las 4 implementaciones (lo que pide la Parte 1).
    for metodo in METODOS_LISTA:
        fig, ax = plt.subplots(figsize=(6.4, 4.2))
        for i, lista in enumerate(LISTAS):
            serie(ax, datos, (lista, metodo), lista, i)
        estilo_ejes(ax, f"List · {metodo}")
        ax.legend(fontsize=8, frameon=False)
        guardar(fig, f"parte1_{metodo}.png")

    # Una gráfica por implementación con todos sus métodos (como el ejemplo de la guía).
    principales = ["PushFront", "PushBack", "PopFront", "PopBack", "Find", "Erase",
                   "AddBefore", "AddAfter"]
    for lista in LISTAS:
        fig, ax = plt.subplots(figsize=(6.4, 4.2))
        for i, metodo in enumerate(principales):
            serie(ax, datos, (lista, metodo), metodo, i)
        estilo_ejes(ax, lista)
        ax.legend(fontsize=7, frameon=False, ncol=2)
        guardar(fig, f"parte1_impl_{lista}.png")


def parte2(datos):
    for estructura, metodos in (("ArrayStack", METODOS_PILA), ("ArrayQueue", METODOS_COLA)):
        fig, ax = plt.subplots(figsize=(6.4, 4.2))
        for i, metodo in enumerate(metodos):
            serie(ax, datos, (estructura, metodo), metodo, i)
        estilo_ejes(ax, f"{estructura} (arreglo dinámico circular)")
        ax.legend(fontsize=8, frameon=False, ncol=2)
        guardar(fig, f"parte2_{estructura}.png")

    fig, axes = plt.subplots(1, 2, figsize=(11, 4.2))
    for ax, (estructura, metodo) in zip(axes, (("ArrayStack", "push"), ("ArrayQueue", "enqueue"))):
        serie(ax, datos, (estructura, f"{metodo}_amortizado"),
              f"{metodo} amortizado (n inserciones ÷ n)", 0)
        serie(ax, datos, (estructura, metodo), f"{metodo} sin resize (una llamada)", 2)
        serie(ax, datos, (estructura, f"{metodo}_peor_caso"),
              f"{metodo} peor caso (dispara el resize)", 1)
        estilo_ejes(ax, f"{estructura}: costo de {metodo}")
        ax.legend(fontsize=8, frameon=False)
    guardar(fig, "parte2_resize.png")


def parte3(datos):
    def grilla(pares, nombre, columnas):
        filas = math.ceil(len(pares) / columnas)
        fig, axes = plt.subplots(filas, columnas, figsize=(4.6 * columnas, 3.6 * filas),
                                 squeeze=False)
        for k, (titulo, lado_lista, lado_arreglo) in enumerate(pares):
            ax = axes[k // columnas][k % columnas]
            serie(ax, datos, lado_lista, f"{lado_lista[0]}.{lado_lista[1]}", 0)
            serie(ax, datos, lado_arreglo, f"{lado_arreglo[0]}.{lado_arreglo[1]}", 1)
            estilo_ejes(ax, titulo, etiquetas=(k % columnas == 0 or k // columnas == filas - 1))
            if k % columnas != 0:
                ax.set_ylabel("")
            if k // columnas != filas - 1:
                ax.set_xlabel("")
            ax.legend(fontsize=7, frameon=False)
        for k in range(len(pares), filas * columnas):
            axes[k // columnas][k % columnas].axis("off")
        fig.tight_layout()
        guardar(fig, nombre)

    grilla(PARES_PILA, "parte3_list_vs_stack.png", 3)
    grilla(PARES_COLA, "parte3_list_vs_queue.png", 3)


def main():
    os.makedirs(SALIDA, exist_ok=True)
    datos = cargar()
    parte1(datos)
    parte2(datos)
    parte3(datos)
    print(f"Gráficas en {SALIDA}")


if __name__ == "__main__":
    main()
