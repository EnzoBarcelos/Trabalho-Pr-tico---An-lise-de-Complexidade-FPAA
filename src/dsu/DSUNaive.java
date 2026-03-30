package dsu;

public class DSUNaive implements DSU {

    private int[] pai;
    private long contador;

    public DSUNaive() {
        this.contador = 0;
    }

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
        // sobe ate a raiz sem nenhuma otimizacao
        while (pai[x] != x) {
            contador++; // acesso ao pai[x] na comparacao
            x = pai[x];
            contador++; // acesso ao pai[x] na atribuicao
        }
        contador++; // ultimo acesso quando pai[x] == x
        return x;
    }

    @Override
    public void union(int x, int y) {
        int raizX = find(x);
        int raizY = find(y);

        if (raizX == raizY) return;

        // simplesmente pendura uma raiz na outra, sem criterio
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
