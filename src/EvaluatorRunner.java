import java.io.*;
import java.util.*;


/**
 * @author dimz
 * @since 5/4/18.
 */
public class EvaluatorRunner {

    private static final int EXECUTION_REPEATS = 5; // how many repeats vertexes test will be conducted
    //test
    //private static int[] vertexesCounts = {50, 50}; // // testing data sample sizes
    // actual
    private static int[] vertexesCounts = {200, 1500}; //  // testing data sample sizes

    /* list of double, which holds two lists:
       list of vertices and list of edges
     */
    // test
    //private static int[] graphDensities = {1, 10};
    // actual
    private static int[] graphDensities = {2, 55};

    private static List<
            Tuple<List<String>,
                    List<LinkedHashSet<EdgeLabel<String>>>>> vertexesLabelsEdges =
            new ArrayList<>(graphDensities.length);

    private static String[] graphImplementations = {"SampleImplementation", "AdjMatrix", "IndMatrix", "IndMatrix_swap"};

    private static long startTime;
    private static long endTime;

    static {
        startTime = System.nanoTime(); // start measurement for vertexes addition to a graph
        initData();
        endTime = System.nanoTime();
        System.out.println(" Init Time: " + ((double) (endTime - startTime)) / Math.pow(10, 9) + " sec");
    }


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {

        scen1_Growing();
        scen2_Neighbours();
        scen3_Shrinking();
    }

    @SuppressWarnings("Duplicates")
    private static void initData() {
        // init vertexes sets
        for (int dataSetLength = vertexesCounts[0]; dataSetLength <= vertexesCounts[1]; dataSetLength += 500) {
            System.out.println("\n\rVertexes collection size: " + dataSetLength);

            // init list of vertexes and edges of specific density
            List<String> vertecesLabels = new ArrayList<>(dataSetLength);
            // add all facebook vert
            String facebookFile = "facebook_combined.txt";
            Set<String> facebookLabels = new LinkedHashSet<>();
            try {

                BufferedReader reader = new BufferedReader(new FileReader(facebookFile));

                String line;
                String delimiter = " ";
                String[] tokens;
                String srcLabel, tarLabel;

                while ((line = reader.readLine()) != null) {
                    tokens = line.split(delimiter);
                    srcLabel = tokens[0];
                    tarLabel = tokens[1];
                    facebookLabels.add(srcLabel);
                    facebookLabels.add(tarLabel);
                }
            } catch (FileNotFoundException ex) {
                System.err.println("File " + facebookFile + " not found.");
            } catch (IOException ex) {
                System.err.println("Cannot open file " + facebookFile);
            }
            // ad ll facebook vertices
            vertecesLabels.addAll(facebookLabels);

            // trailing zeros for string formatter, based on how many leading zeros to add
            String stringformat = String.format("%%0%dd", ((Integer) dataSetLength).toString().length() - 1);

            // create vertexes list
            for (int i = 0; i < dataSetLength; i++) {
                String vertexIntegerSuffix = String.format(stringformat, i);
                vertecesLabels.add(randomString(vertexIntegerSuffix));
            }

            // init edges collections of all possible densities
            List<LinkedHashSet<EdgeLabel<String>>> edgesCollections = new ArrayList<>(graphDensities.length);
            for (int densInt = graphDensities[0]; densInt <= graphDensities[1]; densInt += 7) { // for each density
                double density = densInt * 0.01;

                // how many edges collection to have for vertexes set density
                int totalEdgesForDensity = (int) (dataSetLength * (dataSetLength - 1) * density / 2);
                // set for all edges in this density, each vertex must have at least one edge
                LinkedHashSet<EdgeLabel<String>> edgesDensity = new LinkedHashSet<>(totalEdgesForDensity);
                // total count of edges for each vertex = E-total / V-total
                int edgeDensityConnectionsCount = totalEdgesForDensity / dataSetLength;

                for (String vertex : //for each vertex, add vertexes random edge
                        vertecesLabels) {
                    String connectionVertex;
                    for (int i = 0; i < edgeDensityConnectionsCount; i++) {
                        EdgeLabel<String> edgeConnection;
                        do {
                            do {
                                connectionVertex = vertecesLabels.get(new Random().nextInt(vertecesLabels.size()));
                            } while (vertex.equals(connectionVertex));
                            edgeConnection = new EdgeLabel<>(vertex, connectionVertex);
                        }
                        while (edgesDensity.contains(edgeConnection) || edgesDensity.contains(new EdgeLabel<>(connectionVertex, vertex)));
                        edgesDensity.add(edgeConnection);
                    }
                }
                System.out.println("Density: " + density + " Edges in collection: " + edgesDensity.size());
                edgesCollections.add(edgesDensity);
            }
            vertexesLabelsEdges.add(new Tuple<>(vertecesLabels, edgesCollections));
        }
        System.out.println();
    }

    @SuppressWarnings("Duplicates")
    private static void initFacebookdata(FriendshipGraph<String> graph) {
        // init facebook data
        //String filePath = new File("").getAbsolutePath();
        String facebookFile = "facebook_combined.txt";
        try {

            BufferedReader reader = new BufferedReader(new FileReader(facebookFile));

            String line;
            String delimiter = " ";
            String[] tokens;
            String srcLabel, tarLabel;

            while ((line = reader.readLine()) != null) {
                tokens = line.split(delimiter);
                srcLabel = tokens[0];
                tarLabel = tokens[1];
                try {
                    graph.addEdge(srcLabel, tarLabel);
                } catch (IllegalArgumentException E) {
                    // facebook data not intact
                    System.err.println("Who cares " + srcLabel + " " + tarLabel + " " + E.getMessage());
                }

            }
        } catch (FileNotFoundException ex) {
            System.err.println("File " + facebookFile + " not found.");
        } catch (IOException ex) {
            System.err.println("Cannot open file " + facebookFile);
        }
    }


    @SuppressWarnings("unchecked")
    private static void scen1_Growing() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        List<String> CSVline;
        String scenFileName = "Scen1.csv";
        // run repeated test

        for (String className :    //for each graph type
                graphImplementations) {
            System.out.println("\n\r" + className);
            // for each vertex / edge collection
            outerloop:
            for (Tuple<List<String>, List<LinkedHashSet<EdgeLabel<String>>>> vertexEdgeColl : vertexesLabelsEdges) {
                System.out.println("\n\rvertexes: " + vertexEdgeColl.vertexes.size());

                List<Double> vertexAdditions = new LinkedList<>();

                //for each collection of edges with the same amount of verts
                for (LinkedHashSet<EdgeLabel<String>> edges
                        : vertexEdgeColl.edges) {
                    List<Double> edgesAdditions = new LinkedList<>(); // time measurements for edges addiitons
                    System.out.println("\n\redges " + edges.size());
                    CSVline = new LinkedList<>();
                    CSVline.add(className);
                    CSVline.add(String.valueOf(vertexEdgeColl.vertexes.size()));
                    CSVline.add(String.valueOf(edges.size()));

                    for (int k = 0; k < EXECUTION_REPEATS; k++) { // 5 times
                        // create new graph of collection from it's name
                        Object matrix = Class.forName(className).newInstance();
                        FriendshipGraph<String> graph = (FriendshipGraph<String>) matrix;

                        // add vertices to graph, facebook and randoms
                        startTime = System.nanoTime(); // start measurement for vertexes addition to a graph
                        for (String vertexLabel : vertexEdgeColl.vertexes) {
                            graph.addVertex(vertexLabel);
                        }
                        endTime = System.nanoTime();
                        double timeVertexAddition = ((double) (endTime - startTime)) / Math.pow(10, 9);
                        vertexAdditions.add(timeVertexAddition);

                        // add facebook edges
                        initFacebookdata(graph);
                        // add edges to graph
                        startTime = System.nanoTime(); // start measurement for edges addition to a graph
                        for (EdgeLabel<String> edgeLabel : edges) {
                            try { // add edges. can throw out of memory error
                                graph.addEdge(edgeLabel.getStartLabel(), edgeLabel.getEndLabel());
                            } catch (OutOfMemoryError mem) {
                                break outerloop;
                            }
                        }
                        endTime = System.nanoTime();
                        double timeEdgeAdditions = ((double) (endTime - startTime)) / Math.pow(10, 9);
                        edgesAdditions.add(timeEdgeAdditions);
                    }
                    double averageEdgesAdditions = calculateAverageTime(edgesAdditions);
                    System.out.println("Edge Average=" + averageEdgesAdditions);
                    CSVline.add(String.valueOf(averageEdgesAdditions));

                    CSVUtils.writeLine(CSVline, scenFileName);
                }
                double averageVertexAdditions = calculateAverageTime(vertexAdditions);
                System.out.println("Vertex Average =" + averageVertexAdditions);
                System.out.println();
                CSVline = new LinkedList<>();
                CSVline.add("vertex average");
                CSVline.add(String.valueOf(averageVertexAdditions));
            }
            System.out.println();
            System.out.println();
        }
    }

    @SuppressWarnings("unchecked")
    private static void scen2_Neighbours() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        List<String> CSVline;
        String scenFileName = "Scen2.csv";

        // get edge / vertex tuple with smallest verteces data size (some implementations run out of memory)
        Tuple<List<String>, List<LinkedHashSet<EdgeLabel<String>>>> vertexEdgeColl = vertexesLabelsEdges.get(0);

        System.out.println("Vertexes: " + vertexEdgeColl.vertexes.size());
        // run repeated test
        for (String className :    //for each graph type
                graphImplementations) {
            System.out.println("\n\r" + className);
            outerloop:
            //for each collection of edges with the same amount of verts
            for (LinkedHashSet<EdgeLabel<String>> edges
                    : vertexEdgeColl.edges) {
                System.out.println("\n\rEdges: " + edges.size());


                // create new graph of collection from it's name
                Object matrix = Class.forName(className).newInstance();
                FriendshipGraph<String> graph = (FriendshipGraph<String>) matrix;

                // add vertices to graph, facebook and randoms
                for (String vertexLabel : vertexEdgeColl.vertexes) {
                    graph.addVertex(vertexLabel);
                }

                // add facebook edges
                initFacebookdata(graph);

                // add edges to graph
                for (EdgeLabel<String> edgeLabel : edges) {
                    try { // add edges. can throw out of memory error
                        graph.addEdge(edgeLabel.getStartLabel(), edgeLabel.getEndLabel());
                    } catch (OutOfMemoryError mem) {
                        break outerloop;
                    }
                }
                String largestListRoot = "";
                String largestListLeaf = "";
                List<String> neigbours;
                CSVline = new LinkedList<>();
                CSVline.add(className);
                CSVline.add(String.valueOf(vertexEdgeColl.vertexes.size()));
                CSVline.add(String.valueOf(edges.size()));

                List<Double> allNeigboursTraversalTimes = new LinkedList<>(); // time measurements
                List<Double> shortestDistanceSearchTimes = new LinkedList<>(); // time measurements
                for (int k = 0; k < EXECUTION_REPEATS; k++) { // 5 times
                    // find largest subgraph
                    startTime = System.nanoTime();
                    //for each vertex get neigbours
                    int largestNeigboursListSize = 0;
                    for (String vertexLabel : vertexEdgeColl.vertexes) {
                        neigbours = graph.neighbours(vertexLabel);
                        if (neigbours.size() > largestNeigboursListSize) {
                            largestNeigboursListSize = neigbours.size();
                            largestListRoot = vertexLabel;
                        }
                    }
                    endTime = System.nanoTime();

                    double timeAllNeigboursTraversal = ((double) (endTime - startTime)) / Math.pow(10, 9);
                    allNeigboursTraversalTimes.add(timeAllNeigboursTraversal);

                    // search for longest distance pair
                    int longestDistance = 0;
                    neigbours = graph.neighbours(largestListRoot);
                    for (String vertex : neigbours) {
                        int distance = graph.shortestPathDistance(largestListRoot, vertex);
                        if (longestDistance < distance) {
                            longestDistance = distance;
                            largestListLeaf = vertex;
                        }
                    }

                    // measure the shortest distance time for the furtherst pair
                    startTime = System.nanoTime();
                    int distance = graph.shortestPathDistance(largestListRoot, largestListLeaf);
                    endTime = System.nanoTime();
                    double timeShortestDistanceSearch = ((double) (endTime - startTime)) / Math.pow(10, 9);
                    shortestDistanceSearchTimes.add(timeShortestDistanceSearch);
                }
                double averageAllNeigboursTraversl = calculateAverageTime(allNeigboursTraversalTimes);
                System.out.println("All neig. average=" + averageAllNeigboursTraversl);
                CSVline.add(String.valueOf(averageAllNeigboursTraversl));
                double averageShortestDistanceSearchTime = calculateAverageTime(shortestDistanceSearchTimes);
                System.out.println("Shortest dist average=" + averageShortestDistanceSearchTime);
                CSVline.add(String.valueOf(averageShortestDistanceSearchTime));
                CSVUtils.writeLine(CSVline, scenFileName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void scen3_Shrinking() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {

        List<String> CSVline;
        String scenFileName = "Scen3.csv";

        for (String className :    //for each graph type
                graphImplementations) {
            System.out.println("\n\r" + className);

            // get edge / vertex tuple with largest data size
            Tuple<List<String>, List<LinkedHashSet<EdgeLabel<String>>>> vertexEdgeColl = vertexesLabelsEdges.get(vertexesLabelsEdges.size() - 1);

            System.out.println("\r\nVertexes: " + vertexEdgeColl.vertexes.size());

            // build vertexes collection to remove.
            // removing all even index vertexes, make list of candidates
            List<String> removingVertexesList = new ArrayList<>(vertexEdgeColl.vertexes.size() / 2 + 1);
            for (int i = 1; i < vertexEdgeColl.vertexes.size(); i += 2) {
                removingVertexesList.add(vertexEdgeColl.vertexes.get(i));
            }
            outerloop:
            //for each collection/density of edges
            for (LinkedHashSet<EdgeLabel<String>> edges
                    : vertexEdgeColl.edges) {
                System.out.println("\r\nEdges: " + edges.size());

                CSVline = new LinkedList<>();
                CSVline.add(className);
                CSVline.add(String.valueOf(vertexEdgeColl.vertexes.size()));
                CSVline.add(String.valueOf(edges.size()));

                // build collection of edges to be removed.
                int i = 0;
                Set<EdgeLabel<String>> edgesForRemoval = new LinkedHashSet<>();
                for (EdgeLabel<String> edge : edges) {
                    if ((i % 10) == 0) edgesForRemoval.add(edge);
                    i++;
                }
                System.out.println("Removing Edges: " + edgesForRemoval.size());
                System.out.println("Removing Vertexes: " + removingVertexesList.size());

                CSVline.add(String.valueOf(edgesForRemoval.size()));
                CSVline.add(String.valueOf(removingVertexesList.size()));

                // run repeated test
                List<Double> removeVertTimes = new LinkedList<>();
                List<Double> removeEdgeTimes = new LinkedList<>();
                for (int k = 0; k < EXECUTION_REPEATS; k++) { // 5 times
                    System.out.println("\r\nRep #" + (k + 1));

                    // create new graph of collection from it's name
                    Object matrix = Class.forName(className).newInstance();
                    FriendshipGraph<String> graph = (FriendshipGraph<String>) matrix;
                    // add vertexes to graph
                    for (String vertexLabel : vertexEdgeColl.vertexes) {
                        graph.addVertex(vertexLabel);
                    }

                    // add facebook edges
                    initFacebookdata(graph);

                    // add edges to graph
                    for (EdgeLabel<String> edgeLabel : edges) {
                        try { // add edges. can throw out of memory error
                            graph.addEdge(edgeLabel.getStartLabel(), edgeLabel.getEndLabel());
                        } catch (OutOfMemoryError mem) {
                            break outerloop;
                        }
                    }

                    startTime = System.nanoTime();
                    //remove edges
                    for (EdgeLabel<String> edge : edgesForRemoval) {
                        graph.removeEdge(edge.getStartLabel(), edge.getEndLabel());
                    }
                    endTime = System.nanoTime();
                    double removeEdgeTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
                    System.out.println("Edges Time: " + removeEdgeTime + " sec");
                    removeEdgeTimes.add(removeEdgeTime);

                    startTime = System.nanoTime();
                    // remove vertexes
                    for (String vertex : removingVertexesList) {
                        graph.removeVertex(vertex);
                    }
                    endTime = System.nanoTime();
                    double removeVertTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
                    System.out.println("Vertexes Time: " + removeVertTime + " sec");
                    removeVertTimes.add(removeVertTime);
                }
                double averageEdgesTime = calculateAverageTime(removeEdgeTimes);
                System.out.println("Edges Average Time " + averageEdgesTime);
                CSVline.add(String.valueOf(averageEdgesTime));
                double averageVertTime = calculateAverageTime(removeVertTimes);
                System.out.println("Vert average time " + averageVertTime);
                CSVline.add(String.valueOf(averageVertTime));
                CSVUtils.writeLine(CSVline, scenFileName);
            }
        }


    }

    /**
     * Create vertexes random string of upper / lower case combination with trailing int
     *
     * @param vertexInteger suffix of current iteration
     * @return vertexes unique random vertex label
     */
    private static String randomString(String vertexInteger) {
        int leftLimitLC = 97; //  'a'
        int rightLimitLC = 122; //  'z'
        int leftLimitUP = 65; // 'A'
        int rightLimitUP = 90; // Z
        int targetStringLength = 2; // 5 * 2
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedIntLC = random.nextInt(rightLimitLC - leftLimitLC + 1) + leftLimitLC;
            int randomLimitedIntUC = random.nextInt(rightLimitUP - leftLimitUP + 1) + leftLimitUP;
            buffer.append((char) randomLimitedIntLC);
            buffer.append((char) randomLimitedIntUC);
        }
        buffer.append(vertexInteger);
        return buffer.toString();
    }


    private static Double calculateAverageTime(List<Double> execTime) {
        Double sum = 0.0;
        for (Double time :
                execTime) {
            sum += time;
        }
        return sum / execTime.size();
    }

}

class Tuple<T, K> {
    T vertexes;
    K edges;

    Tuple(T a, K edges) {
        this.vertexes = a;
        this.edges = edges;
    }
}

class EdgeLabel<T> {
    private final String edgeLabel;
    private final T startLabel;
    private final T endLabel;

    EdgeLabel(T startLabel, T endLabel) {

        this.startLabel = startLabel;
        this.endLabel = endLabel;
        edgeLabel = startLabel.toString() + " " + endLabel.toString();

    }

    T getStartLabel() {
        return startLabel;
    }

    T getEndLabel() {
        return endLabel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        // if comparing to self
        if (obj == this) {
            return true;
        }
        // check if correct type
        //typecast obj to T so we can compare using methods

        if (obj instanceof EdgeLabel) {
            return this.getStartLabel().equals(((EdgeLabel) obj).getStartLabel()) &&
                    this.getEndLabel().equals(((EdgeLabel) obj).getEndLabel());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return startLabel.hashCode() * endLabel.hashCode() * prime;
    }

    @Override
    public String toString() {
        return edgeLabel;
    }
}

/**
 * reusing my old code
 */
class CSVUtils {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    private CSVUtils() {
    }


    static void writeLine(List<String> values, String filename) throws IOException {
        try (Writer w = new FileWriter(filename, true)) {
            boolean firstVal = true;
            for (String val : values) {
                if (!firstVal) {
                    w.write(DEFAULT_SEPARATOR);
                }
                w.write(DEFAULT_QUOTE);
                for (int i = 0; i < val.length(); i++) {
                    char ch = val.charAt(i);
                    if (ch == DEFAULT_QUOTE) {
                        w.write(DEFAULT_QUOTE);  //extra quote
                    }
                    w.write(ch);
                }
                w.write(DEFAULT_QUOTE);
                firstVal = false;
            }
            w.write("\r\n");
        }
    }

}