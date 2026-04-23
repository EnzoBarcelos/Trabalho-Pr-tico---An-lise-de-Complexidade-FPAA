package benchmark;

import dsu.*;
import grafo.Aresta;
import grafo.Kruskal;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {

    private static final int[] TAMANHOS = {1000, 5000, 10000, 50000, 100000};

    public static void executar() {
        System.out.println("=== BENCHMARK - Comparacao de DSU no Kruskal ===");
        System.out.println();

        // fase de warm-up: descarta a primeira rodada de cada variante pra dar
        // tempo ao JIT compilar os caminhos quentes antes das medicoes oficiais
        System.out.println("Executando warm-up do JIT...");
        DSU[] warmup = { new DSUNaive(), new DSURank(), new DSUTarjan() };
        List<Aresta> warmupGrafo = gerarGrafoAleatorio(5000, 15000, 99L);
        for (DSU dsu : warmup) {
            Kruskal.executar(5000, new ArrayList<>(warmupGrafo), dsu);
        }
        System.out.println("Warm-up concluido.");
        System.out.println();

        // cabecalho CSV
        System.out.println("n,m,variante,tempo_ms,operacoes");

        for (int n : TAMANHOS) {
            int m = n * 3;
            // seed fixa = 42 para que todas as variantes processem o mesmo grafo
            List<Aresta> arestas = gerarGrafoAleatorio(n, m, 42L);

            DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

            for (DSU dsu : variantes) {
                List<Aresta> copia = new ArrayList<>(arestas);
                dsu.resetContador();

                long inicio = System.nanoTime();
                Kruskal.executar(n, copia, dsu);
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                long ops = dsu.getContador();

                System.out.printf(Locale.US, "%d,%d,%s,%.2f,%d%n", n, m, dsu.getNome(), tempoMs, ops);
            }
        }

        System.out.println();
        System.out.println("=== Benchmark finalizado ===");
    }

    // versao com media de 5 execucoes em grafos diferentes (seeds distintas)
    // agora tambem calcula desvio padrao amostral e grava CSV pro Python consumir
    public static void executarDetalhado() {
        System.out.println("=== BENCHMARK DETALHADO (media de 5 execucoes, seeds distintas) ===");
        System.out.println();

        // warm-up antes de qualquer medicao
        DSU[] warmup = { new DSUNaive(), new DSURank(), new DSUTarjan() };
        List<Aresta> warmupGrafo = gerarGrafoAleatorio(5000, 15000, 99L);
        for (DSU dsu : warmup) {
            Kruskal.executar(5000, new ArrayList<>(warmupGrafo), dsu);
        }

        long[] seeds = {42L, 43L, 44L, 45L, 46L};

        System.out.printf("%-10s %-8s %-30s %-22s %-25s%n",
                "n", "m", "Variante", "Tempo (ms)", "Ops");
        System.out.println("-".repeat(100));

        List<String> linhasCsv = new ArrayList<>();
        linhasCsv.add("n,m,variante,tempo_medio,tempo_std,ops_media,ops_std");

        for (int n : TAMANHOS) {
            int m = n * 3;

            DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

            for (DSU dsu : variantes) {
                double[] tempos = new double[seeds.length];
                double[] ops = new double[seeds.length];

                for (int i = 0; i < seeds.length; i++) {
                    // grafo diferente a cada repeticao (seed distinta)
                    List<Aresta> arestas = gerarGrafoAleatorio(n, m, seeds[i]);
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

                System.out.printf(Locale.US,
                        "%-10d %-8d %-30s %8.2f +/- %-8.2f  %14.0f +/- %-10.0f%n",
                        n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd);

                linhasCsv.add(String.format(Locale.US, "%d,%d,%s,%.4f,%.4f,%.2f,%.2f",
                        n, m, dsu.getNome(), tempoMedio, tempoStd, opsMedia, opsStd));
            }
            System.out.println();
        }

        // grava o CSV na raiz do projeto pro gerar_graficos.py consumir
        try (PrintWriter csv = new PrintWriter(new FileWriter("benchmark_detalhado.csv"))) {
            for (String linha : linhasCsv) csv.println(linha);
        } catch (IOException e) {
            System.err.println("Falha ao gravar CSV: " + e.getMessage());
            return;
        }
        System.out.println("CSV gravado em benchmark_detalhado.csv (" + (linhasCsv.size() - 1) + " linhas)");
    }

    // media simples (aritmetica)
    private static double media(double[] valores) {
        double s = 0;
        for (double v : valores) s += v;
        return s / valores.length;
    }

    // desvio padrao amostral (divide por n-1)
    private static double desvioPadrao(double[] valores, double media) {
        if (valores.length < 2) return 0.0;
        double soma = 0;
        for (double v : valores) {
            double d = v - media;
            soma += d * d;
        }
        return Math.sqrt(soma / (valores.length - 1));
    }

    // gera grafo conexo aleatorio com seed configuravel
    static List<Aresta> gerarGrafoAleatorio(int n, int m, long seed) {
        Random rand = new Random(seed);
        List<Aresta> arestas = new ArrayList<>();

        // garante conectividade criando uma arvore geradora primeiro
        for (int i = 1; i < n; i++) {
            int j = rand.nextInt(i);
            double peso = rand.nextDouble() * 100;
            arestas.add(new Aresta(i, j, peso));
        }

        // arestas extras para atingir m total
        int extras = m - (n - 1);
        for (int k = 0; k < extras; k++) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (u != v) {
                double peso = rand.nextDouble() * 100;
                arestas.add(new Aresta(u, v, peso));
            }
        }

        return arestas;
    }
}
