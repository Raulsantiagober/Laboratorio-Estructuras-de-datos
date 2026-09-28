import edu.unal.ed.benchmark.Config;
import edu.unal.ed.benchmark.CsvWriter;
import edu.unal.ed.benchmark.Inputs;
import edu.unal.ed.benchmark.ListBenchmark;
import edu.unal.ed.benchmark.StackQueueBenchmark;
import edu.unal.ed.benchmark.Summary;
import edu.unal.ed.list.DoublyLinkedListNoTail;
import edu.unal.ed.list.DoublyLinkedListWithTail;
import edu.unal.ed.list.MyList;
import edu.unal.ed.list.SinglyLinkedListNoTail;
import edu.unal.ed.list.SinglyLinkedListWithTail;
import edu.unal.ed.queue.ArrayQueue;
import edu.unal.ed.stack.ArrayStack;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Orquesta las mediciones del laboratorio y escribe los resultados en CSV.
 *
 * <p>Adaptado del Main.java guía del profesor: se conserva la idea de {@code exec(size, method,
 * operation)} con la interfaz {@link Operation}, pero se mide con {@link System#nanoTime()} en vez
 * de {@code Instant/Duration} en milisegundos, porque las operaciones O(1) duran decenas de
 * nanosegundos y en milisegundos casi todas las mediciones darían 0.
 *
 * <p>Este programa NO grafica: solo escribe {@code results/tiempos.csv}. Las gráficas las genera
 * {@code results/graficar.py} después, así ninguna operación de graficación queda dentro de una
 * sección cronometrada.
 *
 * <p>Uso: {@code java -Xmx3g -XX:+UseParallelGC -cp bin Main [--max=10000000] [--out=results]}
 */
public class Main {

    /** Implementaciones de List comparadas en la Parte 1 (en el orden de la guía). */
    private static final Map<String, Supplier<MyList<Integer>>> LISTS = new LinkedHashMap<>();

    static {
        LISTS.put("SinglyLinkedListNoTail", SinglyLinkedListNoTail::new);
        LISTS.put("SinglyLinkedListWithTail", SinglyLinkedListWithTail::new);
        LISTS.put("DoublyLinkedListNoTail", DoublyLinkedListNoTail::new);
        LISTS.put("DoublyLinkedListWithTail", DoublyLinkedListWithTail::new);
    }

    /**
     * Versión adaptada del {@code exec} del profesor: ejecuta {@code operation} {@code size} veces
     * bajo un mismo cronómetro y retorna el tiempo total en nanosegundos.
     */
    public static long exec(int size, String method, Operation operation) {
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            operation.apply(i);
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) throws IOException {
        int max = 10_000_000;
        Path out = Path.of("results");
        for (String arg : args) {
            if (arg.startsWith("--max=")) {
                max = Integer.parseInt(arg.substring("--max=".length()).replace("_", ""));
            } else if (arg.startsWith("--out=")) {
                out = Path.of(arg.substring("--out=".length()));
            } else {
                throw new IllegalArgumentException("Argumento desconocido: " + arg);
            }
        }
        Config config = new Config(max);
        writeEnvironment(out.resolve("entorno.txt"), config);

        // Calentamiento de la JVM: el JIT compila los métodos antes de medir de verdad.
        // Estos resultados se descartan.
        long w0 = System.nanoTime();
        // Dos pasadas completas hasta n = 10^4 para que el JIT optimice todo el código medido.
        Config warmup = new Config(10_000);
        for (int pass = 0; pass < 2; pass++) {
            runAll(warmup, warmup.sizes(), null);
        }
        System.out.printf("Calentamiento terminado en %.1f s%n", (System.nanoTime() - w0) / 1e9);

        String file = "tiempos.csv";
        long t0 = System.nanoTime();
        try (CsvWriter csv = new CsvWriter(out.resolve(file))) {
            runAll(config, config.sizes(), csv);
        }
        System.out.printf("Medición completa en %.1f s. Resultados en %s%n",
                (System.nanoTime() - t0) / 1e9, out.resolve(file));
        // Imprime los sumideros para que el JIT no descarte cálculos.
        System.out.println("(control " + (sinkTotal() & 0xF) + ")");
    }

    private static void runAll(Config config, List<Integer> sizes, CsvWriter csv) {
        Random rnd = new Random(Config.SEED);
        boolean log = csv != null;

        // ---------------- Parte 1: las 4 listas ----------------
        for (int n : sizes) {
            Integer[] values = Inputs.shuffledValues(n, rnd);
            int reps = config.singleOpReps(n);
            for (Map.Entry<String, Supplier<MyList<Integer>>> impl : LISTS.entrySet()) {
                measureList(impl.getKey(), impl.getValue(), values, reps, rnd, csv);
                if (log) {
                    System.out.printf("Parte 1  %-26s n=%,d%n", impl.getKey(), n);
                }
            }
        }

        // ---------------- Parte 2: MyStack y MyQueue ----------------
        for (int n : sizes) {
            Integer[] values = Inputs.shuffledValues(n, rnd);
            int reps = config.singleOpReps(n);
            int rebuild = config.rebuildReps(n);
            measureStack(values, reps, rnd, csv);
            measureQueue(values, reps, rnd, csv);

            // Peor caso: la inserción que dispara la duplicación de capacidad.
            System.gc();
            record(csv, "2-Resize", "ArrayStack", "push_peor_caso",
                    n, StackQueueBenchmark.measureResizeWorstCase(false, values, rebuild));
            System.gc();
            record(csv, "2-Resize", "ArrayQueue", "enqueue_peor_caso",
                    n, StackQueueBenchmark.measureResizeWorstCase(true, values, rebuild));

            // Costo amortizado: n inserciones desde vacío bajo un mismo cronómetro (patrón
            // exec del profesor), dividido entre n. Incluye todas las duplicaciones.
            long[] stackAm = new long[rebuild];
            long[] queueAm = new long[rebuild];
            for (int r = 0; r < rebuild; r++) {
                stackAm[r] = buildTime(values, true);
                queueAm[r] = buildTime(values, false);
            }
            record(csv, "2-Resize", "ArrayStack", "push_amortizado", n, stackAm);
            record(csv, "2-Resize", "ArrayQueue", "enqueue_amortizado", n, queueAm);
            if (log) {
                System.out.printf("Parte 2  ArrayStack/ArrayQueue       n=%,d%n", n);
            }
        }
    }

    // Cada estructura grande se crea dentro de su propio método: al retornar deja de ser
    // alcanzable y el recolector puede liberarla antes de construir la siguiente.

    private static void measureList(String name, Supplier<MyList<Integer>> factory,
                                    Integer[] values, int reps, Random rnd, CsvWriter csv) {
        System.gc();
        MyList<Integer> list = ListBenchmark.build(factory, values);
        for (String method : ListBenchmark.METHODS) {
            long[] samples = ListBenchmark.measure(list, method, values, reps, rnd);
            record(csv, "1-List", name, method, values.length, samples);
        }
    }

    private static void measureStack(Integer[] values, int reps, Random rnd, CsvWriter csv) {
        System.gc();
        ArrayStack<Integer> stack = StackQueueBenchmark.buildStack(values);
        for (String method : StackQueueBenchmark.STACK_METHODS) {
            long[] samples = StackQueueBenchmark.measureStack(stack, method, values, reps, rnd);
            record(csv, "2-StackQueue", "ArrayStack", method, values.length, samples);
        }
    }

    private static void measureQueue(Integer[] values, int reps, Random rnd, CsvWriter csv) {
        System.gc();
        ArrayQueue<Integer> queue = StackQueueBenchmark.buildQueue(values, rnd);
        for (String method : StackQueueBenchmark.QUEUE_METHODS) {
            long[] samples = StackQueueBenchmark.measureQueue(queue, method, values, reps, rnd);
            record(csv, "2-StackQueue", "ArrayQueue", method, values.length, samples);
        }
    }

    /**
     * Tiempo por inserción (ns) al construir la pila (stack = true) o la cola con n inserciones
     * desde vacío.
     */
    private static long buildTime(Integer[] values, boolean stack) {
        System.gc();
        int n = values.length;
        long total;
        if (stack) {
            ArrayStack<Integer> s = new ArrayStack<>();
            total = exec(n, "push", i -> s.push(values[i]));
            sink += s.size();
        } else {
            ArrayQueue<Integer> q = new ArrayQueue<>();
            total = exec(n, "enqueue", i -> q.enqueue(values[i]));
            sink += q.size();
        }
        return total / n;
    }

    private static long sink;

    private static long sinkTotal() {
        return sink;
    }

    private static void record(CsvWriter csv, String part, String structure, String method,
                               int n, long[] samples) {
        if (csv != null) {
            csv.write(part, structure, method, n, Summary.of(samples));
        }
    }

    private static void writeEnvironment(Path file, Config config) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        Runtime rt = Runtime.getRuntime();
        long ramMb = -1;
        if (ManagementFactory.getOperatingSystemMXBean()
                instanceof com.sun.management.OperatingSystemMXBean os) {
            ramMb = os.getTotalMemorySize() / (1024 * 1024);
        }
        String cpu = System.getenv("PROCESSOR_IDENTIFIER");
        String text = String.join(System.lineSeparator(),
                "fecha=" + LocalDateTime.now(),
                "java.version=" + System.getProperty("java.version"),
                "java.vm.name=" + System.getProperty("java.vm.name"),
                "os=" + System.getProperty("os.name") + " " + System.getProperty("os.version")
                        + " (" + System.getProperty("os.arch") + ")",
                "cpu=" + (cpu != null ? cpu : "desconocido"),
                "procesadores=" + rt.availableProcessors(),
                "ram_mb=" + ramMb,
                "heap_max_mb=" + rt.maxMemory() / (1024 * 1024),
                "resolucion_nanotime_ns=" + nanoTimeResolution(),
                "tamanos=" + config.sizes(),
                "semilla=" + Config.SEED,
                "");
        Files.writeString(file, text, StandardCharsets.UTF_8);
    }

    /**
     * Menor diferencia positiva entre dos llamadas seguidas a System.nanoTime(): la resolución
     * práctica del reloj en esta máquina (sirve para justificar la unidad de medida).
     */
    private static long nanoTimeResolution() {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 1_000_000; i++) {
            long a = System.nanoTime();
            long b = System.nanoTime();
            while (b == a) {
                b = System.nanoTime();
            }
            best = Math.min(best, b - a);
        }
        return best;
    }
}
