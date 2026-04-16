import dsu.*;
import grafo.Aresta;
import grafo.Kruskal;
import benchmark.Benchmark;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Trabalho Pratico - DSU / Union-Find");
            System.out.println("  FPAA - PUC Minas");
            System.out.println("========================================");
            System.out.println("1 - Exemplo de Kruskal (grafo pequeno)");
            System.out.println("2 - Benchmark (CSV)");
            System.out.println("3 - Benchmark detalhado (tabela com medias)");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");

            int opcao = sc.nextInt();

            switch (opcao) {
                case 1:
                    exemploKruskal();
                    break;
                case 2:
                    Benchmark.executar();
                    break;
                case 3:
                    Benchmark.executarDetalhado();
                    break;
                case 0:
                    System.out.println("Encerrando...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    // roda kruskal num grafo simples pra mostrar que funciona
    private static void exemploKruskal() {
        System.out.println();
        System.out.println("--- Exemplo: Grafo com 6 vertices ---");

        // grafo de exemplo
        //   0 --4-- 1 --2-- 2
        //   |       |       |
        //   1       3       5
        //   |       |       |
        //   3 --6-- 4 --7-- 5

        List<Aresta> arestas = new ArrayList<>();
        arestas.add(new Aresta(0, 1, 4));
        arestas.add(new Aresta(0, 3, 1));
        arestas.add(new Aresta(1, 2, 2));
        arestas.add(new Aresta(1, 4, 3));
        arestas.add(new Aresta(2, 5, 5));
        arestas.add(new Aresta(3, 4, 6));
        arestas.add(new Aresta(4, 5, 7));

        DSU[] variantes = { new DSUNaive(), new DSURank(), new DSUTarjan() };

        for (DSU dsu : variantes) {
            // copia pra cada execucao
            List<Aresta> copia = new ArrayList<>(arestas);

            Kruskal.ResultadoKruskal resultado =
                    Kruskal.executarDetalhado(6, copia, dsu);

            System.out.println();
            System.out.println("Variante: " + dsu.getNome());
            System.out.println("Peso total da MST: " + resultado.pesoTotal);
            System.out.println("Operacoes de acesso: " + resultado.operacoes);
            System.out.print("Arestas da MST: ");
            for (Aresta a : resultado.arestas) {
                System.out.print(a + "  ");
            }
            System.out.println();
        }
    }
}
