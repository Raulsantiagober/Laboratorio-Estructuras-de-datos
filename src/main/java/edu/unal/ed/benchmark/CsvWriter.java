package edu.unal.ed.benchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Escribe una fila de CSV por experimento. Se llama SIEMPRE fuera de las secciones cronometradas.
 */
public final class CsvWriter implements AutoCloseable {

    public static final String HEADER =
            "parte,estructura,metodo,n,repeticiones,media_ns,media_recortada_ns,mediana_ns,min_ns,max_ns,desv_ns";

    private final PrintWriter out;

    public CsvWriter(Path file) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        out = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8));
        out.println(HEADER);
    }

    public void write(String part, String structure, String method, int n, Summary s) {
        out.println(String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%.1f,%.1f,%.1f,%d,%d,%.1f",
                part, structure, method, n, s.reps(), s.mean(), s.trimmedMean(), s.median(),
                s.min(), s.max(), s.stdDev()));
        out.flush();
    }

    @Override
    public void close() {
        out.close();
    }
}
