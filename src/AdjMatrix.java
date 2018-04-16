import java.io.PrintWriter;
import java.util.*;


/**
 * Adjacency matrix implementation for the FriendshipGraph interface.
 * <p>
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2016.
 */
@SuppressWarnings("TypeParameterExplicitlyExtendsObject")
public class AdjMatrix<T extends Object> implements FriendshipGraph<T> {

    private List<T> Vl;  // vertices list
    private Set<T> Vs; // vert Set
    private boolean[][] adjMatrix;

    /**
     * Constructs empty graph.
     */
    AdjMatrix() {

        Vl = new ArrayList<>();
        Vs = new HashSet<>();
        adjMatrix = new boolean[Vl.size() + 1][Vl.size() + 1];

    } // end of AdjMatrix()


    /**
     * Construct graph with set boundaries
     */
    void AdjMatrixresize(int size) {
        adjMatrix = new boolean[size][size];
    }

    /**
     * A better way of doing this would be to anticipate the addition of more vertices
     * and create vertexes new array of 2*|V| size
     * every time we fill up more than 1/2 the current array.
     * This will limit the number of instances where we have to resize our matrix.
     */
    public void addVertex(T vertLabel) {
        // If the number of vertices is more than half the size of matrix,
        // double the size of matrix
        if (!Vs.contains(vertLabel)) { //if not contains
            Vl.add(vertLabel);
            Vs.add(vertLabel);
            int size = adjMatrix[0].length; // get size of matrix
            if (Vl.size() > 0.5 * size) {
                size = 2 * size;
                boolean[][] newAdjMatrix = new boolean[size][size];
                for (int i = 0; i < adjMatrix.length; i++) {
                    newAdjMatrix[i] = Arrays.copyOf(adjMatrix[i], size);
                }
                adjMatrix = newAdjMatrix;
            }
        }

    } // end of addVertex()

    @SuppressWarnings("Duplicates")
    public void removeVertex(T vertLabel) {

        if (Vs.contains(vertLabel)) {
            int removeAtIndex = Vl.indexOf(vertLabel);
            Vl.remove(vertLabel);
            Vs.remove(vertLabel);
            // shift row up
            for (int i = 0; i < Vl.size(); i++) {
                if (i >= removeAtIndex) {
                    if (adjMatrix[i + 1] != null) {
                        adjMatrix[i] = adjMatrix[i + 1];
                    } else {
                        adjMatrix[i] = new boolean[adjMatrix[0].length];
                    }
                }
                // shift column left
                System.arraycopy(adjMatrix[i], removeAtIndex + 1,
                        adjMatrix[i], removeAtIndex, adjMatrix[i].length - removeAtIndex - 1);
            }
            // resize matrix
            int size = adjMatrix[0].length;
            int numV = Vl.size();
            if (numV < 0.5 * size) {
                size = (int) (0.5 * size);
                boolean[][] newAdjMatrix = new boolean[size][size];
                for (int i = 0; i < size; i++) {
                    newAdjMatrix[i] = Arrays.copyOf(adjMatrix[i], size);
                }
                adjMatrix = newAdjMatrix;
            }
        }
    } // end of removeVertex()

    public void addEdge(T srcLabel, T tarLabel) {
        if (Vs.contains(srcLabel) && Vs.contains(tarLabel)){
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            adjMatrix[srcLabelIndex][tarLabelIndex] = true;
            adjMatrix[tarLabelIndex][srcLabelIndex] = true;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of addEdge()

    public void removeEdge(T srcLabel, T tarLabel) {
        if (Vs.contains(srcLabel) && Vs.contains(tarLabel)) {
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            adjMatrix[srcLabelIndex][tarLabelIndex] = false;
            adjMatrix[tarLabelIndex][srcLabelIndex] = false;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of removeEdges()

    public ArrayList<T> neighbours(T vertLabel) {
        if (Vs.contains(vertLabel)){
            int vertLabelIndex = Vl.indexOf(vertLabel);
            ArrayList<T> neighbours = new ArrayList<>();
            for (int i = 0; i < Vl.size(); i++) {
                if (adjMatrix[vertLabelIndex][i]) neighbours.add(Vl.get(i));
            }
            return neighbours;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of neighbours()

    public void printVertices(PrintWriter os) {
        Iterator<T> it = Vl.iterator();
        T vertice;
        if (it.hasNext()) {
            vertice = it.next();
            os.print(vertice.toString());
        }
        while (it.hasNext()) {
            vertice = it.next();
            os.print(" " + vertice);
        }
        os.println();
    } // end of printVertices()

    public void printEdges(PrintWriter os) {
        for (int i = 0; i < Vl.size(); i++) {
            for (int j = 0; j < Vl.size(); j++) {
                if (adjMatrix[i][j]) os.println(Vl.get(i) + " " + Vl.get(j));
            }
        }
    } // end of printEdges()

    @SuppressWarnings("Duplicates")
    public int shortestPathDistance(T vertLabel1, T vertLabel2) {
        if (Vs.contains(vertLabel1) && Vs.contains(vertLabel2)) {
            HashSet<T> visitedV = new HashSet<>();
            ArrayDeque<VertexDistance<T>> bfsForrest = new ArrayDeque<>();
            visitedV.add(vertLabel1);
            bfsForrest.add(new VertexDistance<>(vertLabel1, 0));

            while (!bfsForrest.isEmpty()) { //while traversal forrest has vertexes
                VertexDistance<T> queueHeadVertDistance = bfsForrest.remove(); //remove head of queue, as we've visited
                T headVertex = queueHeadVertDistance.getMyVertex();
                int travelDistFromRoot = queueHeadVertDistance.getDistance();
                if (headVertex.equals(vertLabel2)) {
                    return travelDistFromRoot;
                }

                for (T nextVertex : this.neighbours(headVertex)) {
                    if (!visitedV.contains(nextVertex)) {
                        visitedV.add(nextVertex);
                        bfsForrest.add(new VertexDistance<>(nextVertex, travelDistFromRoot + 1));
                    }
                }
            }
            // if we reach this point, source and target are disconnected
            return disconnectedDist;
        } else throw new IllegalArgumentException("Vertex does not exist.");

    } // end of shortestPathDistance()

} // end of class AdjMatrix