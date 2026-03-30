package dsu;

public class DSURank implements DSU {

    private int[] pai;
    private int[] rank;
    private long contador;

    public DSURank() {
        this.contador = 0;
    }

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
        // sobe ate a raiz, igual ao naive (sem compressao)
        while (pai[x] != x) {
            contador++;
            x = pai[x];
            contador++;
        }
        contador++;
        return x;
    }

    @Override
    public void union(int x, int y) {
        int raizX = find(x);
        int raizY = find(y);

        if (raizX == raizY) return;

        // une pela rank: arvore menor vai pra baixo da maior
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
        return "Union by Rank";
    }
}
