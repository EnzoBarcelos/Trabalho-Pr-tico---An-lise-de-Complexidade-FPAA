package dsu;

public interface DSU {

    void makeSet(int n);

    int find(int x);

    void union(int x, int y);

    long getContador();

    void resetContador();

    String getNome();
}
