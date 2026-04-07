package grafo;

import dsu.DSU;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {

    public static double executar(int numVertices, List<Aresta> arestas, DSU dsu) {
        return executarDetalhado(numVertices, arestas, dsu).pesoTotal;
    }

    public static ResultadoKruskal executarDetalhado(int numVertices, List<Aresta> arestas, DSU dsu) {
        dsu.makeSet(numVertices);

        List<Aresta> ordenadas = new ArrayList<>(arestas);
        Collections.sort(ordenadas);

        List<Aresta> mst = new ArrayList<>();
        double pesoTotal = 0.0;

        for (Aresta aresta : ordenadas) {
            int raizU = dsu.find(aresta.u);
            int raizV = dsu.find(aresta.v);

            if (raizU == raizV) {
                continue;
            }

            dsu.union(aresta.u, aresta.v);
            mst.add(aresta);
            pesoTotal += aresta.peso;

            if (mst.size() == numVertices - 1) {
                break;
            }
        }

        return new ResultadoKruskal(mst, pesoTotal, dsu.getContador());
    }

    public static class ResultadoKruskal {
        public final List<Aresta> arestas;
        public final double pesoTotal;
        public final long operacoes;

        public ResultadoKruskal(List<Aresta> arestas, double pesoTotal, long operacoes) {
            this.arestas = arestas;
            this.pesoTotal = pesoTotal;
            this.operacoes = operacoes;
        }
    }
}
