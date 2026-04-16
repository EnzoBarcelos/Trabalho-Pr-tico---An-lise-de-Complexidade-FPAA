package dsu;

/**
 * Interface base para as implementações de Disjoint Set Union (Union-Find).
 *
 * Política de contagem de operações (getContador):
 *   - Cada leitura do array pai[] conta como 1 acesso.
 *   - Cada escrita no array pai[] (union ou path compression) conta como 1 acesso.
 *   - Essa política é uniforme nas três variantes (Naive, Rank, Tarjan),
 *     permitindo comparação direta do número de acessos a memória.
 */
public interface DSU {

    void makeSet(int n);

    int find(int x);

    void union(int x, int y);

    /** Retorna o total de acessos (leituras + escritas) ao array pai[]. */
    long getContador();

    void resetContador();

    String getNome();
}
