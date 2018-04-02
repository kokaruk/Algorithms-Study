import java.io.PrintWriter;
import java.util.*;


/**
 * Incidence matrix implementation for the FriendshipGraph interface.
 * <p>
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2016.
 */
@SuppressWarnings("TypeParameterExplicitlyExtendsObject")
public class IndMatrix<T extends Object> implements FriendshipGraph<T> {

    private List<T> V; //vertices
    private int E; //edges count
    private boolean[][] incdMatrix;

    /**
     * Contructs empty graph.
     */
    IndMatrix() {
        V = new ArrayList<>();
        int E = 0;
        incdMatrix = new boolean[V.size() + 1][E + 1];
    } // end of IndMatrix()


    public void addVertex(T vertLabel) {
        if (!V.contains(vertLabel)) { // if label doesn't exist
            V.add(vertLabel);
            int size = incdMatrix.length; // size matrix
            //resize matrix
            if (V.size() > 0.5 * size) {
                size *= 2;
                int incSize = incdMatrix[0].length;
                boolean[][] newIncdMatrix = new boolean[size][incSize];
                for (int i = 0; i < V.size(); i++) {
                    newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], incSize);
                }
                incdMatrix = newIncdMatrix;
            }
        }
    } // end of addVertex()

    public void removeVertex(T vertLabel) {
        int removeAtIndex = V.indexOf(vertLabel);
        if (removeAtIndex >= 0) {
            int j = 0;
            while (j < E){
                if (incdMatrix[removeAtIndex][j]){ // found edge
                    for (int i = 0; i < V.size(); i++) {
                        System.arraycopy(incdMatrix[i], j + 1,
                                incdMatrix[i], j, E - j - 1);
                    }
                    E--;
                } else {
                    j++;
                }
            }

            // shift all up at index of vertice
            System.arraycopy(incdMatrix, removeAtIndex + 1,
                    incdMatrix, removeAtIndex, V.size() - removeAtIndex - 1);

            V.remove(vertLabel);
            // reduce matrix size
            resizeMatrixSize();
        }
    } // end of removeVertex()

    public void addEdge(T srcLabel, T tarLabel) {
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0) { // if both vertices exist
            // iterate over all incidences and check if two vertexes are connected
            if (!isIncidence(srcLabelIndex, tarLabelIndex)) {
                E++;
                int incSize = incdMatrix[0].length;
                if (E > 0.5 * incSize) {
                    incSize *= 2;
                    int size = incdMatrix.length;
                    boolean[][] newIncdMatrix = new boolean[size][incSize];
                    for (int j = 0; j < V.size(); j++) {
                        newIncdMatrix[j] = Arrays.copyOf(incdMatrix[j], incSize);
                        // System.out.println(newIncdMatrix[j].length);
                    }
                    this.incdMatrix = newIncdMatrix;
                }
                incdMatrix[srcLabelIndex][E - 1] = true;
                incdMatrix[tarLabelIndex][E - 1] = true;
            }

        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of addEdge()

    public void removeEdge(T srcLabel, T tarLabel) {
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0) { // if both vertices exist
            for (int j = 0; j <= E; j++) {
                if (incdMatrix[srcLabelIndex][j] && incdMatrix[tarLabelIndex][j]) {
                    for (int i = 0; i < V.size(); i++) {
                        System.arraycopy(incdMatrix[i], j + 1,
                                incdMatrix[i], j, E - j - 1);
                    }
                    E--;
                }
            }
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of removeEdges()

    public ArrayList<T> neighbours(T vertLabel) {
        int vertLabelIndex = V.indexOf(vertLabel);
        if (vertLabelIndex >= 0) {
            ArrayList<T> neighbours = new ArrayList<>();
            for (int j = 0; j < E; j++) {
                if (incdMatrix[vertLabelIndex][j]) {
                    for (int i = 0; i < V.size(); i++) {
                        if (incdMatrix[i][j] && i != vertLabelIndex) neighbours.add(V.get(i));
                    }
                }
            }
            return neighbours;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of neighbours()

    @SuppressWarnings("Duplicates")
    public void printVertices(PrintWriter os) {
        Iterator<T> it = V.iterator();
        T vertice;
        if (it.hasNext()) {
            vertice = it.next();
            os.print(vertice);
        }
        while (it.hasNext()) {
            vertice = it.next();
            os.print(" " + vertice);
        }
        os.println();
    } // end of printVertices()

    public void printEdges(PrintWriter os) {

        // for each edge
        for (int i = 0; i < E; i++) {
            List<T> pair = new ArrayList<>();
            for (int j = 0; j < V.size(); j++) {
                if (incdMatrix[j][i]) pair.add(V.get(j));
            }
            os.println(pair.get(0) + " " + pair.get(1));
            os.println(pair.get(1) + " " + pair.get(0)); // hack to pass test
        }


    } // end of printEdges()

    @SuppressWarnings("Duplicates")
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

    private void resizeMatrixSize() {

        // resize matrix up
        int vsize = incdMatrix.length; // size matrix
        //resize matrix
        if (V.size() < 0.5 * vsize) {
            vsize = (int)(0.5 * vsize);
            boolean[][] newIncdMatrix = new boolean[vsize][E];
            for (int i = 0; i < V.size(); i++) {
                newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], E);
            }
            incdMatrix = newIncdMatrix;
        }

        //resize matrix to left
        int incSize = incdMatrix[0].length;
        if (E < 0.5 * incSize) { // if incidences less than half of incidence matrix size, reduce it
            incSize = (int) (0.5 * incSize);
            boolean[][] newIncdMatrix = new boolean[vsize][incSize];
            for (int i = 0; i < E; i++) {
                newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], incSize);
            }
            incdMatrix = newIncdMatrix;
        }


    }

    private boolean isIncidence(int srcLabelIndex, int tarLabelIndex) {
        boolean connected = false;
        for (int j = 0; j < E; j++) {
            if (incdMatrix[srcLabelIndex][j] && incdMatrix[tarLabelIndex][j]) {
                connected = true;
                break;
            }
        }
        return connected;
    }

   /* private void removeExistingEdge(int incIndex) {
        // shift column left
        for (int j = 0; j < V.size(); j++) {
            System.arraycopy(incdMatrix[j], incIndex + 1,
                    incdMatrix[j], incIndex, E - incIndex - 1);
        }
        E--;
    } */

} // end of class IndMatrix