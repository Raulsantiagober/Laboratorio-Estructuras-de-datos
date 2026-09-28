package edu.unal.ed.benchmark;

import java.util.Random;

/** Generación de entradas aleatorias (fuera de las secciones cronometradas). */
public final class Inputs {

    private Inputs() {
    }

    /**
     * Permutación aleatoria de 0..n-1 ya convertida a Integer (Fisher-Yates). El autoboxing se
     * hace aquí, antes de medir, para que el costo de crear los Integer no contamine los tiempos.
     */
    public static Integer[] shuffledValues(int n, Random rnd) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Integer tmp = values[i];
            values[i] = values[j];
            values[j] = tmp;
        }
        return values;
    }
}
