package grafo;

import dsu.DSU;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {

    // executa kruskal e retorna o peso total da MST
    public static double executar(int numVertices, List<Aresta> arestas, DSU dsu) {
        dsu.makeSet(numVertices);

        // ordena as arestas pelo peso
        Collections.sort(arestas);

        double pesoTotal = 0;
        int arestasAdicionadas = 0;

        for (Aresta a : arestas) {
            // se nao estao no mesmo conjunto, une
            if (dsu.find(a.u) != dsu.find(a.v)) {
                dsu.union(a.u, a.v);
                pesoTotal += a.peso;
                arestasAdicionadas++;

                // MST completa quando tem n-1 arestas
                if (arestasAdicionadas == numVertices - 1) {
                    break;
                }
            }
        }

        return pesoTotal;
    }

    // versao que retorna tambem as arestas da MST (util pra verificacao)
    public static ResultadoKruskal executarDetalhado(int numVertices, List<Aresta> arestas, DSU dsu) {
        dsu.makeSet(numVertices);
        Collections.sort(arestas);

        List<Aresta> mst = new ArrayList<>();
        double pesoTotal = 0;

        for (Aresta a : arestas) {
            if (dsu.find(a.u) != dsu.find(a.v)) {
                dsu.union(a.u, a.v);
                mst.add(a);
                pesoTotal += a.peso;

                if (mst.size() == numVertices - 1) break;
            }
        }

        return new ResultadoKruskal(mst, pesoTotal, dsu.getContador());
    }

    // classe auxiliar pro resultado detalhado
    public static class ResultadoKruskal {
        public List<Aresta> arestas;
        public double pesoTotal;
        public long operacoes;

        public ResultadoKruskal(List<Aresta> arestas, double pesoTotal, long operacoes) {
            this.arestas = arestas;
            this.pesoTotal = pesoTotal;
            this.operacoes = operacoes;
        }
    }
}
