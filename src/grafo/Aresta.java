package grafo;

public class Aresta implements Comparable<Aresta> {

    public int u;
    public int v;
    public double peso;

    public Aresta(int u, int v, double peso) {
        this.u = u;
        this.v = v;
        this.peso = peso;
    }

    @Override
    public int compareTo(Aresta outra) {
        return Double.compare(this.peso, outra.peso);
    }

    @Override
    public String toString() {
        return "(" + u + " - " + v + ") peso: " + peso;
    }
}
