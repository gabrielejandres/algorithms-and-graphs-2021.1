public class AlgGrafos {
    public static void main(String[] args) {
        Graph graph = new Graph();
        FileController fileController = new FileController("myfiles" + java.io.File.separator + "grafo01.txt");

        // Lê o grafo do arquivo e valida se o arquivo está no formato especificado
        fileController.readGraph(graph);

        // Verifica os requisitos do grafo de entrada. Deve ser não direcionado
        graph.isUndirected();

        // Verifica se o grafo é cacto
        graph.isCactusGraph();
    }
}