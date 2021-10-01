import java.util.*;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;

public class Graph {
    private ArrayList<Graph> biconnectedComponents; // lista de componentes biconexas (blocos) do grafo
    private Stack<Vertex> stack; // pilha usada na detecção de componentes biconexas (blocos)
    protected HashMap<Integer, Vertex> vertexSet; // conjunto de vértices do grafo
    private int time;

    /**
     * Construtor do grafo
     */
    public Graph() {
        this.vertexSet = new HashMap<>();
        this.biconnectedComponents = new ArrayList<>();
    }

    /**
     * Imprime dados do grafo
     */
    public void printGraph() {
        for(Vertex v : vertexSet.values()) v.printVertex();
    }

    /**
     * Adiciona um novo vértice ao grafo
     * @param id identificador do vértice
     */
    public void addVertex(int id) {
        if (id > 0 && !this.vertexSet.containsKey(id)) {
            Vertex v = new Vertex(id);
            vertexSet.put(v.id, v);
        }
        else {
            System.out.println("Id " + id + " inválido ou já utilizado!");
        }
    }

    /**
     * Adiciona um arco no grafo. Função auxiliar usada para criar arestas. Como o grafo é não-direcionado, sempre teremos arcos duplos
     * @param from vértice de origem
     * @param to vértice de chegada
     */
    protected void addArc(Integer from, Integer to) {
        Vertex vertex1 = this.vertexSet.get(from);
        Vertex vertex2 = this.vertexSet.get(to);

        // Verifica se há laços (auto-loop)
        if (Objects.equals(vertex1.id, vertex2.id)) {
            System.out.println("O grafo de entrada não pode ter laços.");
            System.exit(1);
        }

        // Se já existe aresta não adiciona
        if (!vertex1.neighborhood.containsKey(vertex2.id)){
            vertex1.addNeighbor(vertex2);
        } else {
            System.out.println("Já existe aresta entre os vértices " + vertex1.id + " e " + vertex2.id);
        }
    }

    /**
     * Adiciona uma aresta ao grafo
     * @param from vértice de origem
     * @param to vértice de chegada
     */
    public void addEdge(Integer from, Integer to) {
        if(!this.vertexSet.containsKey(from)) this.addVertex(from);
        if(!this.vertexSet.containsKey(to)) this.addVertex(to);

        // Cria arco nas duas direções para que seja aresta
        this.addArc(from, to);
        this.addArc(to, from);
    }

    /**
     * Cria as componentes biconexas (blocos) do grafo.
     * Os blocos serão úteis devido ao fato de um grafo cacto ser um grafo conexo onde todos os blocos são arestas ou ciclos simples
     */
    public void biconnectedComponents( ) {
        Graph biconnectedComponent = new Graph(); // cria um grafo para armazenar o subgrafo correspondente à componente biconexa (bloco)

        for(Vertex vertex : this.vertexSet.values( )) {
            if(vertex.d == null) {
                this.stack = new Stack<>( );
                this.biconnectedComponentVisit(vertex);
            }

            // Há componente biconexa na pilha
            if (!this.stack.empty( )) {
                biconnectedComponent = new Graph();
                this.biconnectedComponents.add(biconnectedComponent);
            }

            // Atribui os dados presentes na pilha para a componente biconexa (bloco)
            while (!this.stack.empty( )) {
                Vertex v2 = this.stack.pop();
                Vertex v1 = this.stack.pop();
                biconnectedComponent.addEdge( v1.id, v2.id );
            }
        }
    }

    /**
     * Auxiliar para a função de criação de componentes conexas. Busca em profundidade no grafo.
     * @param vertex vértice de partida
     */
    private void biconnectedComponentVisit(Vertex vertex) {
        vertex.d = ++this.time;
        vertex.low = vertex.d;

        for(Vertex neighbor : vertex.neighborhood.values( ) ) {
            if(neighbor.d == null) {
                this.stack.push(vertex); // se não foi descoberto, adiciona a aresta de árvore
                this.stack.push(neighbor);
                neighbor.parent = vertex;
                this.biconnectedComponentVisit(neighbor);

                if(neighbor.low < vertex.low)
                    vertex.low = neighbor.low;

                // Detecta componente biconexa
                if(neighbor.low >= vertex.d)
                    this.createBiconnectedComponent(vertex, neighbor);
            } else if(neighbor != vertex.parent) {
                if(neighbor.d < vertex.d) {  // se é aresta de retorno
                    this.stack.push(vertex);
                    this.stack.push(neighbor);
                    if(neighbor.d < vertex.low){
                        vertex.low = neighbor.d;
                    }
                }
                // else aresta já explorada
            }
        }
    }

    /**
     * Cria uma componente conexa
     * @param cutVertex vértice de corte
     * @param auxiliarVertex vértice auxiliar
     */
    private void createBiconnectedComponent(Vertex cutVertex, Vertex auxiliarVertex) {
        Graph biconnectedComponent = new Graph(); // cria um grafo para armazenar o subgrafo correspondente à componente biconexa (bloco)
        Vertex vertex1, vertex2;

        this.biconnectedComponents.add(biconnectedComponent);
        vertex2 = this.stack.pop();
        vertex1 = this.stack.pop();

        // Atribui os dados presentes na pilha para a componente biconexa (bloco)
        while(vertex1 != cutVertex || vertex2 != auxiliarVertex) {
            if(this.stack.empty( )) return;
            biconnectedComponent.addEdge(vertex1.id, vertex2.id);
            vertex2 = this.stack.pop();
            vertex1 = this.stack.pop();
        }
        biconnectedComponent.addEdge( vertex1.id, vertex2.id );
    }

    /**
     * Imprime as componentes biconexas do grafo
     */
    public void printBiconnectedComponents() {
        int count = 1;
        for(Graph biconnectedComponent : this.biconnectedComponents) {
            System.out.println("-- Componente conexa " + count++ + " --");
            biconnectedComponent.printGraph();
        }
    }

    /**
     * Verifica se o grafo é cacto.
     * A implementação leva em conta a definição de que um cacto é um grafo conexo no qual todos os blocos são arestas ou ciclos simples.
     * Tendo em mãos todas as componentes biconexas (blocos) do grafo, basta verificarmos os graus dos vértices porque:
     * Se o grau for 1, a componente biconexa (bloco) é uma aresta
     * Se o grau for 2, a componente biconexa (bloco) é um ciclo simples
     * Se o grau for maior que 2 quer dizer que existem ciclos simples que têm vértices em comum
     * Então, no caso em que todos os vértices têm grau 1 ou 2, o grafo é cacto. Caso o grau seja superior a isso, existem arestas em comum entre ciclos e o grafo não é cacto
     */
    public void isCactusGraph() {
        boolean isCactus = true;
        ArrayList<Integer> vertexMultiCycle = new ArrayList<>();

        // Verifica se o grafo é conectado
        if (!this.isConnected()) {
            System.out.println("O grafo não é cacto porque é não-conectado. Um grafo cacto é conectado.");
            System.exit(1);
        }

        for (Graph biconnectedComponent : this.biconnectedComponents) {
            // Iterando sobre os vértices de um bloco para verificar seus graus
            for (Map.Entry<Integer, Vertex> vertex : biconnectedComponent.vertexSet.entrySet()) {
                if (vertex.getValue().degree() > 2) {
                    vertexMultiCycle.add(vertex.getKey());
                    isCactus = false;
                }
            }
        }

        if (isCactus) {
            System.out.println("O grafo fornecido como entrada é um grafo cacto!\nTodo bloco é uma aresta ou ciclo (os vértices das componentes conexas têm grau 1 ou 2)");
        } else {
            System.out.println("O grafo fornecido como entrada não é um grafo cacto.\nOs vértices " + vertexMultiCycle + " fazem parte de mais de um ciclo");
        }

        // this.printBiconnectedComponents();
    }

    /**
     * Busca em profundidade. Utilizada para verificar se o grafo é conectado antes de criar componentes biconexas.
     * @param id identificador do vértice
     */
    public void DFS(int id) {
        Vertex v = vertexSet.get(id); // vértice de partida da busca
        if (v == null) {
            System.out.println("Vértice não existe. Impossível realizar busca.");
            return;
        }
        for (Vertex vertex : vertexSet.values())
            vertex.reset();
        v.visited = true; // marca o vértice inicial
        DFSVisit(v);
    }

    /**
     * Visitador da busca em profundidade
     * @param v vértice
     */
    public void DFSVisit(Vertex v) {
        for (Vertex u : v.neighborhood.values()) { // Percorre os vizinhos do vértice
            if (!u.visited) { // Vértice ainda não marcado
                u.visited = true;
                DFSVisit(u); // A recursão faz com que a busca seja de profundidade.
            }
        }
    }

    /**
     * Verifica se o grafo é conectado
     * @return true, se o grafo for conectado, false, caso contrário
     */
    public boolean isConnected() {
        DFS(1); // busca em profundidade partindo do primeiro vértice

        // Percorre todos os vértices do grafo para saber se foram visitados
        for (Vertex vertex : this.vertexSet.values()) {
            if (!vertex.visited) {
                System.out.println("Vértice " + vertex.id + " não alcançável pela raiz.");
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se o grafo é não-direcionado
     * @return true se o grafo não for direcionado, false caso contrário
     */
    public void isUndirected() {
        boolean isUndirected = true;

        for(Vertex vertex : vertexSet.values()) {
            for(Vertex neighbor : vertex.neighborhood.values()) {
                if (neighbor.neighborhood.get(vertex.id) == null) isUndirected = false;
            }
        }

        if (!isUndirected) {
            System.out.println("Entrada inválida. O grafo de entrada é direcionado.");
            System.exit(1);
        }
    }
}
