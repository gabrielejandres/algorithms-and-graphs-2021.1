import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

public class FileController {
    private String fileName;

    /**
     * Construtor do controlador de arquivos
     * @param fileName nome do arquivo de entrada com os dados do grafo
     */
    FileController(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Lê os dados do grafo do arquivo e atribui à instância
     * @param graph instância do grafo
     */
    public void readGraph(Graph graph) {
        String row;
        String[] data;

        try {
            FileReader fileReader = new FileReader(this.fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int vertexId, neighborVertexId;

            while ((row = bufferedReader.readLine( )) != null) {
                row = row.replaceAll("\\s+", " ").trim(); // remove espaços em branco

                // Verifica se a linha está no padrão
                if (!Pattern.matches("[1-9][0-9]*+\\s+=(\\s+[1-9][0-9]*)*", row)) {
                    System.out.println("As linhas de entrada devem estar no padrão <vertice> = <vizinho1> <vizinho2> ... Por exemplo: 1 = 2 3 4");
                    System.exit(1);
                }

                data = row.split(" ");
                vertexId = Integer.parseInt(data[0]);
                if(!graph.vertexSet.containsKey(vertexId))
                    graph.addVertex(vertexId);

                for(int i = 2; i < data.length; i++) {
                    neighborVertexId = Integer.parseInt(data[i]);
                    if(!graph.vertexSet.containsKey(neighborVertexId))
                        graph.addVertex(neighborVertexId);
                    graph.addArc(vertexId, neighborVertexId);
                }
            }
            bufferedReader.close();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
