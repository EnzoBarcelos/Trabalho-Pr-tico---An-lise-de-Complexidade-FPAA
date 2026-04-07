package benchmark;

import dsu.DSU;
import dsu.DSUNaive;
import dsu.DSURank;
import dsu.DSUTarjan;
import grafo.Aresta;
import grafo.Kruskal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Benchmark {

    private static final int[] TAMANHOS = {1000, 5000, 10000, 50000, 100000};
    private static final int REPETICOES = 5;
    private static final long SEED = 42L;

    public static void executar() {
        System.out.println("=== BENCHMARK - Comparacao de DSU no Kruskal ===");
        System.out.println();
        System.out.println("n,m,variante,tempo_ms,operacoes");

        Random random = new Random(SEED);

        for (int n : TAMANHOS) {
            int m = n * 3;
            List<Aresta> base = gerarGrafoConexo(n, m, random);

            for (DSU dsu : criarVariantes()) {
                List<Aresta> copia = new ArrayList<>(base);

                long inicio = System.nanoTime();
                Kruskal.executar(n, copia, dsu);
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                System.out.printf("%d,%d,%s,%.2f,%d%n", n, m, dsu.getNome(), tempoMs, dsu.getContador());
            }
        }

        System.out.println();
        System.out.println("=== Benchmark finalizado ===");
    }

    public static void executarDetalhado() {
        System.out.println("=== BENCHMARK DETALHADO (media de 5 execucoes) ===");
        System.out.println();

        System.out.printf("%-10s %-8s %-30s %-15s %-15s%n", "n", "m", "Variante", "Tempo(ms)", "Operacoes");
        System.out.println("-".repeat(80));

        Random random = new Random(SEED);

        for (int n : TAMANHOS) {
            int m = n * 3;
            List<List<Aresta>> bases = new ArrayList<>(REPETICOES);

            for (int r = 0; r < REPETICOES; r++) {
                bases.add(gerarGrafoConexo(n, m, random));
            }

            for (DSU dsu : criarVariantes()) {
                double somaTempos = 0.0;
                long somaOperacoes = 0;

                for (int r = 0; r < REPETICOES; r++) {
                    List<Aresta> copia = new ArrayList<>(bases.get(r));

                    long inicio = System.nanoTime();
                    Kruskal.executar(n, copia, dsu);
                    long fim = System.nanoTime();

                    somaTempos += (fim - inicio) / 1_000_000.0;
                    somaOperacoes += dsu.getContador();
                }

                double mediaTempo = somaTempos / REPETICOES;
                long mediaOperacoes = somaOperacoes / REPETICOES;

                System.out.printf("%-10d %-8d %-30s %-15.2f %-15d%n",
                        n, m, dsu.getNome(), mediaTempo, mediaOperacoes);
            }

            System.out.println();
        }
    }

    private static DSU[] criarVariantes() {
        return new DSU[] {
                new DSUNaive(),
                new DSURank(),
                new DSUTarjan()
        };
    }

    private static List<Aresta> gerarGrafoConexo(int n, int m, Random random) {
        if (n <= 0) {
            throw new IllegalArgumentException("n precisa ser positivo");
        }

        if (n == 1) {
            if (m != 0) {
                throw new IllegalArgumentException("com 1 vertice, o grafo so pode ter 0 arestas");
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

        List<Aresta> arestas = new ArrayList<>(m);
        Set<Long> usadas = new HashSet<>();

        for (int i = 1; i < n; i++) {
            int pai = random.nextInt(i);
            adicionarAresta(arestas, usadas, i, pai, random.nextDouble() * 100.0);
        }

        while (arestas.size() < m) {
            int u = random.nextInt(n);
            int v = random.nextInt(n);

            if (u != v) {
                adicionarAresta(arestas, usadas, u, v, random.nextDouble() * 100.0);
            }
        }

        return arestas;
    }

    private static void adicionarAresta(List<Aresta> arestas, Set<Long> usadas, int u, int v, double peso) {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        long chave = chaveAresta(a, b);

        if (usadas.add(chave)) {
            arestas.add(new Aresta(a, b, peso));
        }
    }

    private static long chaveAresta(int u, int v) {
        return ((long) u << 32) | (v & 0xffffffffL);
    }
}
