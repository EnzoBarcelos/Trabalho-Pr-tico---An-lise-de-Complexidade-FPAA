package benchmark;

import dsu.DSU;
import dsu.DSUNaive;
import dsu.DSURank;
import dsu.DSUTarjan;
import grafo.Aresta;
import grafo.Kruskal;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Benchmark {

    private static final int[] TAMANHOS = {1000, 5000, 10000, 50000, 100000};
    private static final int REPETICOES = 5;
    private static final long[] SEEDS = {42L, 43L, 44L, 45L, 46L};

    public static void executar() {
        System.out.println("=== BENCHMARK - Comparacao de DSU no Kruskal ===");
        System.out.println();

        System.out.println("Warm-up do JIT...");
        List<Aresta> warmup = gerarGrafoAleatorio(5000, 15000, 99L);
        for (DSU dsu : novasVariantes()) {
            Kruskal.executar(5000, new ArrayList<>(warmup), dsu);
        }
        System.out.println("Warm-up concluido.");
        System.out.println();

        System.out.println("n,m,variante,tempo_ms,operacoes");

        for (int n : TAMANHOS) {
            int m = n * 3;
            List<Aresta> base = gerarGrafoAleatorio(n, m, 42L);

            for (DSU dsu : novasVariantes()) {
                dsu.resetContador();
                long inicio = System.nanoTime();
                Kruskal.executar(n, new ArrayList<>(base), dsu);
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                System.out.printf(Locale.US, "%d,%d,%s,%.2f,%d%n",
                        n, m, dsu.getNome(), tempoMs, dsu.getContador());
            }
        }

        System.out.println();
        System.out.println("=== Benchmark finalizado ===");
    }

    public static void executarDetalhado() {
        System.out.println("=== BENCHMARK DETALHADO (media de 5 execucoes) ===");
        System.out.println();

        System.out.println("Warm-up do JIT...");
        List<Aresta> warmup = gerarGrafoAleatorio(5000, 15000, 99L);
        for (DSU dsu : novasVariantes()) {
            Kruskal.executar(5000, new ArrayList<>(warmup), dsu);
        }
        System.out.println("Warm-up concluido.");
        System.out.println();

        System.out.printf("%-10s %-8s %-30s %-15s %-15s%n",
                "n", "m", "Variante", "Tempo(ms)", "Operacoes");
        System.out.println("-".repeat(80));

        List<String> linhasCsv = new ArrayList<>();
        linhasCsv.add("n,m,variante,tempo_medio,tempo_std,ops_media,ops_std");

        for (int n : TAMANHOS) {
            int m = n * 3;

            for (DSU dsu : novasVariantes()) {
                double[] tempos = new double[REPETICOES];
                double[] ops = new double[REPETICOES];

                for (int i = 0; i < REPETICOES; i++) {
                    List<Aresta> arestas = gerarGrafoAleatorio(n, m, SEEDS[i]);
                    dsu.resetContador();

                    long inicio = System.nanoTime();
                    Kruskal.executar(n, arestas, dsu);
                    long fim = System.nanoTime();

                    tempos[i] = (fim - inicio) / 1_000_000.0;
                    ops[i] = dsu.getContador();
                }

                double tempoMedio = media(tempos);
                double tempoStd = desvioPadrao(tempos, tempoMedio);
                double opsMedia = media(ops);
                double opsStd = desvioPadrao(ops, opsMedia);

                System.out.printf(Locale.US, "%-10d %-8d %-30s %8.2f +/- %-8.2f  %14.0f +/- %-10.0f%n",
                        n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd);

                linhasCsv.add(String.format(Locale.US, "%d,%d,%s,%.4f,%.4f,%.2f,%.2f",
                        n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd));
            }

            System.out.println();
        }

        try (PrintWriter csv = new PrintWriter(new FileWriter("benchmark_detalhado.csv"))) {
            for (String linha : linhasCsv) {
                csv.println(linha);
            }
        } catch (IOException e) {
            System.err.println("Falha ao gravar CSV: " + e.getMessage());
            return;
        }

        System.out.println("CSV gravado em benchmark_detalhado.csv");
    }

    private static DSU[] novasVariantes() {
        return new DSU[] { new DSUNaive(), new DSURank(), new DSUTarjan() };
    }

    private static double media(double[] valores) {
        double soma = 0.0;
        for (double valor : valores) {
            soma += valor;
        }
        return soma / valores.length;
    }

    private static double desvioPadrao(double[] valores, double media) {
        if (valores.length < 2) {
            return 0.0;
        }

        double soma = 0.0;
        for (double valor : valores) {
            double diff = valor - media;
            soma += diff * diff;
        }
        return Math.sqrt(soma / (valores.length - 1));
    }

    static List<Aresta> gerarGrafoAleatorio(int n, int m, long seed) {
        if (n <= 0) {
            throw new IllegalArgumentException("n precisa ser positivo");
        }

        if (n == 1) {
            if (m != 0) {
                throw new IllegalArgumentException("com 1 vertice o grafo so pode ter 0 arestas");
            }
            return new ArrayList<>();
        }

        long maximo = (long) n * (n - 1) / 2;
        if (m < n - 1) {
            throw new IllegalArgumentException("grafo conexo precisa de pelo menos n - 1 arestas");
        }
        if ((long) m > maximo) {
            throw new IllegalArgumentException("quantidade de arestas acima do limite possivel");
        }

        Random rand = new Random(seed);
        List<Aresta> arestas = new ArrayList<>(m);
        Set<Long> usadas = new HashSet<>();

        for (int i = 1; i < n; i++) {
            int pai = rand.nextInt(i);
            adicionarAresta(arestas, usadas, i, pai, rand.nextDouble() * 100.0);
        }

        while (arestas.size() < m) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (u != v) {
                adicionarAresta(arestas, usadas, u, v, rand.nextDouble() * 100.0);
            }
        }

        return arestas;
    }

    private static void adicionarAresta(List<Aresta> arestas, Set<Long> usadas, int u, int v, double peso) {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        long chave = ((long) a << 32) | (b & 0xffffffffL);

        if (usadas.add(chave)) {
            arestas.add(new Aresta(a, b, peso));
        }
    }
}
