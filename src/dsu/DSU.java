package dsu;

// Interface base para as estruturas de conjuntos disjuntos
public interface DSU {

    void makeSet(int n);

    int find(int x);

    void union(int x, int y);

    // retorna quantos acessos ao array pai foram feitos
    long getContador();

    void resetContador();

    String getNome();
}
