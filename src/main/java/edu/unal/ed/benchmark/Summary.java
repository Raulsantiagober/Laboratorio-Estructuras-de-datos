package edu.unal.ed.benchmark;

import java.util.Arrays;

/**
 * Resumen estadístico de las muestras (en nanosegundos) de un experimento.
 *
 * <p>La medida principal es la <b>media recortada al 10 %</b>: se ordenan las muestras, se
 * descarta el 10 % más bajo y el 10 % más alto (al menos una muestra por extremo si hay 5 o más)
 * y se promedia el resto. Sigue siendo un promedio,
 * pero no se deja arrastrar por pausas aisladas del recolector de basura o del sistema operativo.
 */
public record Summary(int reps, double mean, double trimmedMean, double median,
                      long min, long max, double stdDev) {

    public static Summary of(long[] samples) {
        long[] s = samples.clone();
        Arrays.sort(s);
        int n = s.length;

        double sum = 0;
        for (long v : s) {
            sum += v;
        }
        double mean = sum / n;

        // Con 5 o más muestras se descarta al menos una por cada extremo.
        int cut = n >= 5 ? Math.max(1, (int) (n * 0.10)) : 0;
        double trimmedSum = 0;
        for (int i = cut; i < n - cut; i++) {
            trimmedSum += s[i];
        }
        double trimmed = trimmedSum / (n - 2 * cut);

        double median = (n % 2 == 1) ? s[n / 2] : (s[n / 2 - 1] + s[n / 2]) / 2.0;

        double sq = 0;
        for (long v : s) {
            sq += (v - mean) * (v - mean);
        }
        double std = n > 1 ? Math.sqrt(sq / (n - 1)) : 0;

        return new Summary(n, mean, trimmed, median, s[0], s[n - 1], std);
    }
}
