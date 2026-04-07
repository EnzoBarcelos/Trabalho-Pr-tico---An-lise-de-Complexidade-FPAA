package dsu;

public class DSUTarjan implements DSU {

    private int[] pai;
    private int[] rank;
    private long contador;

    @Override
    public void makeSet(int n) {
        pai = new int[n];
        rank = new int[n];

        for (int i = 0; i < n; i++) {
            pai[i] = i;
            rank[i] = 0;
        }

        contador = 0;
    }

    @Override
    public int find(int x) {
        contador++;
        if (pai[x] != x) {
            pai[x] = find(pai[x]);
            contador++;
        }
        return pai[x];
    }

    @Override
    public void union(int x, int y) {
        int raizX = find(x);
        int raizY = find(y);

        if (raizX == raizY) {
            return;
        }

        if (rank[raizX] < rank[raizY]) {
            pai[raizX] = raizY;
        } else if (rank[raizX] > rank[raizY]) {
            pai[raizY] = raizX;
        } else {
            pai[raizY] = raizX;
            rank[raizX]++;
        }

        contador++;
    }

    @Override
    public long getContador() {
        return contador;
    }

    @Override
    public void resetContador() {
        contador = 0;
    }

    @Override
    public String getNome() {
        return "Tarjan (Rank + Compressao)";
    }
}
