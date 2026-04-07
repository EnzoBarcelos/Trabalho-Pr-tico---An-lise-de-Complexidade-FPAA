import benchmark.Benchmark;
import dsu.DSU;
import dsu.DSUNaive;
import dsu.DSURank;
import dsu.DSUTarjan;
import grafo.Aresta;
import grafo.Kruskal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Trabalho Pratico - DSU / Union-Find");
            System.out.println("  FPAA - PUC Minas");
            System.out.println("========================================");
            System.out.println("1 - Exemplo de Kruskal");
            System.out.println("2 - Benchmark (CSV)");
            System.out.println("3 - Benchmark detalhado");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");

            int opcao = scanner.nextInt();

            if (opcao == 1) {
                exemploKruskal();
            } else if (opcao == 2) {
                Benchmark.executar();
            } else if (opcao == 3) {
                Benchmark.executarDetalhado();
            } else if (opcao == 0) {
                System.out.println("Encerrando...");
                scanner.close();
                return;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void exemploKruskal() {
        System.out.println();
        System.out.println("--- Exemplo com 6 vertices ---");

        List<Aresta> arestas = new ArrayList<>();
        arestas.add(new Aresta(0, 1, 4));
        arestas.add(new Aresta(0, 3, 1));
        arestas.add(new Aresta(1, 2, 2));
        arestas.add(new Aresta(1, 4, 3));
        arestas.add(new Aresta(2, 5, 5));
        arestas.add(new Aresta(3, 4, 6));
        arestas.add(new Aresta(4, 5, 7));

        DSU[] variantes = {
                new DSUNaive(),
                new DSURank(),
                new DSUTarjan()
        };

        for (DSU dsu : variantes) {
            Kruskal.ResultadoKruskal resultado = Kruskal.executarDetalhado(6, new ArrayList<>(arestas), dsu);

            System.out.println();
            System.out.println("Variante: " + dsu.getNome());
            System.out.println("Peso total da MST: " + resultado.pesoTotal);
            System.out.println("Operacoes de acesso: " + resultado.operacoes);
            System.out.print("Arestas da MST: ");
            for (Aresta aresta : resultado.arestas) {
                System.out.print(aresta + "  ");
            }
            System.out.println();
        }
    }
}
