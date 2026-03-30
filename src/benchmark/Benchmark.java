package benchmark;

import dsu.*;
import grafo.Aresta;
import grafo.Kruskal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Benchmark {

    // tamanhos de grafo pra testar
    private static final int[] TAMANHOS = {1000, 5000, 10000, 50000, 100000};

    public static void executar() {
        System.out.println("=== BENCHMARK - Comparacao de DSU no Kruskal ===");
        System.out.println();

        // cabecalho CSV
        System.out.println("n,m,variante,tempo_ms,operacoes");

        for (int n : TAMANHOS) {
            // gera um grafo denso o suficiente pra ter MST
            int m = n * 3; // 3 arestas por vertice em media
            List<Aresta> arestas = gerarGrafoAleatorio(n, m);

            // testa cada variante
            DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

            for (DSU dsu : variantes) {
                // copia as arestas porque o sort modifica a lista
                List<Aresta> copia = new ArrayList<>(arestas);

                long inicio = System.nanoTime();
                double peso = Kruskal.executar(n, copia, dsu);
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                long ops = dsu.getContador();

                System.out.printf("%d,%d,%s,%.2f,%d%n", n, m, dsu.getNome(), tempoMs, ops);
            }
        }

        System.out.println();
        System.out.println("=== Benchmark finalizado ===");
    }

    // teste mais detalhado que roda varias vezes e faz media
    public static void executarDetalhado() {
        System.out.println("=== BENCHMARK DETALHADO (media de 5 execucoes) ===");
        System.out.println();

        int repeticoes = 5;

        System.out.printf("%-10s %-8s %-30s %-15s %-15s%n",
                "n", "m", "Variante", "Tempo(ms)", "Operacoes");
        System.out.println("-".repeat(80));

        for (int n : TAMANHOS) {
            int m = n * 3;

            DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

            for (DSU dsu : variantes) {
                double somaTempos = 0;
                long somaOps = 0;

                for (int r = 0; r < repeticoes; r++) {
                    List<Aresta> arestas = gerarGrafoAleatorio(n, m);

                    long inicio = System.nanoTime();
                    Kruskal.executar(n, arestas, dsu);
                    long fim = System.nanoTime();

                    somaTempos += (fim - inicio) / 1_000_000.0;
                    somaOps += dsu.getContador();
                }

                double mediaTempos = somaTempos / repeticoes;
                long mediaOps = somaOps / repeticoes;

                System.out.printf("%-10d %-8d %-30s %-15.2f %-15d%n",
                        n, m, dsu.getNome(), mediaTempos, mediaOps);
            }
            System.out.println(); // separador entre tamanhos
        }
    }

    // gera grafo conexo aleatorio
    private static List<Aresta> gerarGrafoAleatorio(int n, int m) {
        Random rand = new Random(42); // seed fixa pra reproducibilidade
        List<Aresta> arestas = new ArrayList<>();

        // primeiro garante que o grafo eh conexo criando uma arvore
        for (int i = 1; i < n; i++) {
            int j = rand.nextInt(i); // conecta i a algum vertice anterior
            double peso = rand.nextDouble() * 100;
            arestas.add(new Aresta(i, j, peso));
        }

        // depois adiciona arestas extras aleatorias
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
