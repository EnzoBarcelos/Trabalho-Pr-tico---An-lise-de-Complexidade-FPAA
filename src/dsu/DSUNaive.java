package dsu;

public class DSUNaive implements DSU {

    private int[] pai;
    private long contador;

    @Override
    public void makeSet(int n) {
        pai = new int[n];
        for (int i = 0; i < n; i++) {
            pai[i] = i;
        }
        contador = 0;
    }

    @Override
    public int find(int x) {
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

        if (raizX == raizY) {
            return;
        }

        pai[raizX] = raizY;
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
        return "Naive";
    }
}
