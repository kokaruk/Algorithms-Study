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

    private List<T> V;  // vertices
    private boolean[][] adjMatrix;

    /**
     * Constructs empty graph.
     */
    AdjMatrix() {

        V = new ArrayList<>();
        adjMatrix = new boolean[V.size() + 1][V.size() + 1];

    } // end of AdjMatrix()


    /**
     * A better way of doing this would be to anticipate the addition of more vertices
     * and create a new array of 2*|V| size
     * every time we fill up more than 1/2 the current array.
     * This will limit the number of instances where we have to resize our matrix.
     */
    public void addVertex(T vertLabel) {
        // If the number of vertices is more than half the size of matrix,
        // double the size of matrix
        if (!V.contains(vertLabel)) { //if not contains
            V.add(vertLabel);
            int size = adjMatrix[0].length; // get size of matrix
            if (V.size() > 0.5 * size) {
                size = 2 * size;
                boolean[][] newAdjMatrix = new boolean[size][size];
                for (int i = 0; i < adjMatrix.length; i++) {
                    newAdjMatrix[i] = Arrays.copyOf(adjMatrix[i], size);
                }
                adjMatrix = newAdjMatrix;
            }
        }

    } // end of addVertex()

    public void removeVertex(T vertLabel) {
        int removeAtIndex = V.indexOf(vertLabel);
        if (removeAtIndex >= 0) {
            V.remove(vertLabel);
            // shift row up
            for (int i = 0; i < V.size(); i++) {
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
            int numV = V.size();
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
        // Implement me!
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0) {
            adjMatrix[srcLabelIndex][tarLabelIndex] = true;
            adjMatrix[tarLabelIndex][srcLabelIndex] = true;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of addEdge()

    public void removeEdge(T srcLabel, T tarLabel) {
        // Implement me!
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0) {
            adjMatrix[srcLabelIndex][tarLabelIndex] = false;
            adjMatrix[tarLabelIndex][srcLabelIndex] = false;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of removeEdges()

    public ArrayList<T> neighbours(T vertLabel) {
        ArrayList<T> neighbours = new ArrayList<>();
        int vertLabelIndex = V.indexOf(vertLabel);
        if (vertLabelIndex >= 0) {
            for (int i = 0; i < V.size(); i++) {
                if (adjMatrix[vertLabelIndex][i]) neighbours.add(V.get(i));
            }
        } else throw new IllegalArgumentException("Vertex does not exist.");


        return neighbours;
    } // end of neighbours()

    public void printVertices(PrintWriter os) {
        Iterator<T> it = V.iterator();
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
        for (int i = 0; i < V.size(); i++) {
            for (int j = 0; j < V.size(); j++) {
                if (adjMatrix[i][j]) os.println(V.get(i) + " " + V.get(j));
            }
        }
    } // end of printEdges()

    public int shortestPathDistance(T vertLabel1, T vertLabel2) {
        if (V.contains(vertLabel1) && V.contains(vertLabel2)) {
            HashSet<T> visitedV = new HashSet<>();
            ArrayDeque<Tuple<T>> bfsForrest = new ArrayDeque<>();
            visitedV.add(vertLabel1);
            bfsForrest.add(new Tuple<>(vertLabel1, 0));

            while (!bfsForrest.isEmpty()) { //while traversal forrest has vertexes
                Tuple<T> queueHeadTuple = bfsForrest.remove(); //remove head of queue, as we've visited
                T headVertex = queueHeadTuple.getMyVertex();
                int travelDistFromRoot = queueHeadTuple.getDistance();
                if (headVertex.equals(vertLabel2)) {
                    return travelDistFromRoot;
                }

                for (T nextVertex : this.neighbours(headVertex)) {
                    if (!visitedV.contains(nextVertex)) {
                        visitedV.add(nextVertex);
                        bfsForrest.add(new Tuple<>(nextVertex, travelDistFromRoot + 1));
                    }
                }
            }
            // if we reach this point, source and target are disconnected
            return disconnectedDist;
        } else throw new IllegalArgumentException("Vertex does not exist.");

    } // end of shortestPathDistance()

    private class Tuple<J> {
        private J myVertex;
        private int distance;

        private Tuple(J myVertex, int distance) {
            this.myVertex = myVertex;
            this.distance = distance;
        }

        J getMyVertex() {
            return myVertex;
        }

        int getDistance() {
            return distance;
        }
    }

} // end of class AdjMatrix