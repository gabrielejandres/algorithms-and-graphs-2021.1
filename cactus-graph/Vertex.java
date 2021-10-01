import java.util.HashMap;

public class Vertex {
    protected Integer id;
    protected HashMap<Integer, Vertex> neighborhood;
    protected Vertex parent;
    protected Integer d, low;
    protected boolean visited;

    /**
     * Construtor do vértice
     * @param id identificador do vértice
     */
    public Vertex (int id) {
        this.id = id;
        this.neighborhood = new HashMap<Integer,Vertex>();
        this.parent = null;
        this.d = null;
        this.visited = false;
    }

    /**
     * Imprime dados do vértice
     */
    public void printVertex() {
        System.out.print("\nId do vértice " + id + ", Vizinhança: " );
        for(Vertex v : this.neighborhood.values())
            System.out.print(" " + v.id);
    }

    /**
     * Reseta os dados do vértice
     */
    protected void reset() {
        this.parent = null;
        this.d = null;
    }

    /**
     * Adiciona um novo vizinho ao vértice
     * @param v vértice vizinho
     */
    public void addNeighbor(Vertex v) {
        this.neighborhood.put(v.id, v);
    }

    /**
     * @return grau do vértice
     */
    public int degree() {
        return this.neighborhood.size();
    }
}
