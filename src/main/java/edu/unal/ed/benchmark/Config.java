package edu.unal.ed.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Parámetros de la medición: tamaños de entrada y número de repeticiones por tamaño.
 *
 * <p>Tamaños: los de la guía (10, 100, 10^4, 10^6), con 10^7 como máximo en lugar de 10^8 para
 * que la medición quepa con holgura en un equipo de 8 GB de RAM.
 *
 * <p>Las repeticiones disminuyen con n porque cada repetición deshace su operación (y a veces
 * busca el nodo) fuera del cronómetro, lo que cuesta O(n); con n pequeño hacen falta muchas
 * repeticiones porque cada operación dura pocas decenas de nanosegundos y el ruido pesa más.
 */
public final class Config {

    /** Semilla fija: las entradas son aleatorias pero reproducibles. */
    public static final long SEED = 2026L;

    /** Tamaños de entrada. */
    public static final int[] SIZES = {10, 100, 10_000, 1_000_000, 10_000_000};

    private final int maxSize;

    public Config(int maxSize) {
        this.maxSize = maxSize;
    }

    /** Tamaños de {@link #SIZES} menores o iguales a maxSize. */
    public List<Integer> sizes() {
        List<Integer> sizes = new ArrayList<>();
        for (int n : SIZES) {
            if (n <= maxSize) {
                sizes.add(n);
            }
        }
        return sizes;
    }

    /** Repeticiones para medir UNA operación sobre una estructura de tamaño n. */
    public int singleOpReps(int n) {
        if (n <= 100) {
            return 2_000;
        } else if (n <= 10_000) {
            return 500;
        } else if (n <= 1_000_000) {
            return 50;
        }
        return 20;
    }

    /** Repeticiones para experimentos que reconstruyen la estructura completa (O(n) cada uno). */
    public int rebuildReps(int n) {
        if (n <= 10_000) {
            return 50;
        } else if (n <= 1_000_000) {
            return 10;
        }
        return 5;
    }
}
