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
public class IndMatrix_swap<T extends Object> implements FriendshipGraph<T> {

    private List<T> Vl; //vertices
    private Set<T> Vs; //vertices
    private int E; //edges count
    private boolean[][] incdMatrix;

    /**
     * Contructs empty graph.
     */
    IndMatrix_swap() {
        Vl = new ArrayList<>();
        Vs = new HashSet<>();
        int E = 0;
        incdMatrix = new boolean[Vl.size() + 1][E + 1];
    } // end of IndMatrix()


    public void addVertex(T vertLabel) {
        if (!Vs.contains(vertLabel)) { // if label doesn't exist
            Vl.add(vertLabel);
            Vs.add(vertLabel);
            int size = incdMatrix[0].length; // size matrix
            //resize matrix only if qty of v > 0.5 size
            if (Vl.size() > 0.5 * size) {
                size *= 2;
                boolean[][] newIncdMatrix = new boolean[incdMatrix.length][size];
                for (int i = 0; i < E; i++) {
                    newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], size);
                }
                incdMatrix = newIncdMatrix;
            }
        }
    } // end of addVertex()

    public void removeVertex(T vertLabel) {

        if (Vs.contains(vertLabel)) {
            int removeAtIndex = Vl.indexOf(vertLabel);
            int i = 0;
            while (i < E) {
                if (incdMatrix[i][removeAtIndex]) { // found an incidence from this vertex
                    System.arraycopy(incdMatrix, i + 1,
                            incdMatrix, i, E - i - 1);
                    E--;
                } else {
                    // shift column left
                    System.arraycopy(incdMatrix[i], removeAtIndex + 1,
                            incdMatrix[i], removeAtIndex, Vl.size() - removeAtIndex - 1);
                    i++;
                }
            }

            Vl.remove(removeAtIndex);
            Vs.remove(vertLabel);

            resizeRowsUp();

            // resize matrix to the left
            int size = incdMatrix[0].length;
            if (Vl.size() < 0.5 * size) {
                size = (int) (0.5 * size);
                boolean[][] newIncdMatrix = new boolean[incdMatrix.length][size];
                for (int k = 0; k < incdMatrix.length; k++) {
                    newIncdMatrix[k] = Arrays.copyOf(incdMatrix[k], size);
                }
                incdMatrix = newIncdMatrix;
            }
        }

    } // end of removeVertex()


    public void addEdge(T srcLabel, T tarLabel) {
        if (!srcLabel.toString().equals(tarLabel.toString())
                && Vs.contains(tarLabel) && Vs.contains(tarLabel)) { // if both vertices exist
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            // iterate over all incidences and check if two vertexes are connected
            boolean connected = false;
            for (int j = 0; j <= E; j++) {
                if (incdMatrix[j][srcLabelIndex] && incdMatrix[j][tarLabelIndex]) {
                    connected = true;
                    break;
                }
            }
            if (!connected) {
                E++;
                int incSize = incdMatrix.length;
                if (E >= 0.5 * incSize) {
                    incSize *= 2;
                    int size = incdMatrix[0].length;
                    boolean[][] newIncdMatrix = new boolean[incSize][size];
                    for (int j = 0; j < E; j++) {
                        newIncdMatrix[j] = Arrays.copyOf(incdMatrix[j], size);
                    }
                    incdMatrix = newIncdMatrix;
                }
                boolean[] newIncidence = new boolean[incdMatrix[0].length];
                newIncidence[srcLabelIndex] = true;
                newIncidence[tarLabelIndex] = true;
                incdMatrix[E - 1] = newIncidence;
            }

        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of addEdge()

    public void removeEdge(T srcLabel, T tarLabel) {

        if (Vs.contains(srcLabel) && Vs.contains(tarLabel)) { // if both vertices exist
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            for (int i = 0; i < E; i++) {
                if (incdMatrix[i][srcLabelIndex] && incdMatrix[i][tarLabelIndex]) { // if found connection
                    System.arraycopy(incdMatrix, i + 1,
                            incdMatrix, i, incdMatrix.length - i - 1);
                    E--;
                    break;
                }
            }
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of removeEdges()

    @SuppressWarnings("Duplicates")
    public ArrayList<T> neighbours(T vertLabel) {
        if (Vs.contains(vertLabel)) {
            int vertLabelIndex = Vl.indexOf(vertLabel);
            ArrayList<T> neighbours = new ArrayList<>();
            for (int j = 0; j < E; j++) {
                if (incdMatrix[j][vertLabelIndex]) {
                    for (int i = 0; i < Vl.size(); i++) {
                        if (incdMatrix[j][i] && i != vertLabelIndex) neighbours.add(Vl.get(i));
                    }
                }
            }
            return neighbours;
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of neighbours()

    @SuppressWarnings("Duplicates")
    public void printVertices(PrintWriter os) {
        Iterator<T> it = Vl.iterator();
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

    @SuppressWarnings("Duplicates")
    public void printEdges(PrintWriter os) {
        // below is O(n^4) loop. on an average facebook_test takes 9.5 seconds.
        // test requires to output vertexes pair of edges for each vertex such as 'A B' and 'B A'
        // too long

        /*  for (int i = 0; i < V.size(); i++) {// for each vert
            for (int j = 0; j < E; j++) { // in each incidence
                if (incdMatrix[j][i]) { // if this incidence is true
                    os.print(V.get(i));
                    for (int k = 0; k < V.size(); k++) {
                        if (incdMatrix[j][k] && k != i) os.println(" " + V.get(k));
                    }
                }
            }
        } */

        for (int i = 0; i < E; i++) {
            List<T> pair = new ArrayList<>();
            for (int j = 0; j < Vl.size(); j++) {
                if (incdMatrix[i][j]) pair.add(Vl.get(j));
            }
            os.println(pair.get(0) + " " + pair.get(1));
            os.println(pair.get(1) + " " + pair.get(0)); // hack to pass test
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
                VertexDistance<T> queueHeadVertexDistance = bfsForrest.remove(); //remove head of queue, as we've visited
                T headVertex = queueHeadVertexDistance.getMyVertex();
                int travelDistFromRoot = queueHeadVertexDistance.getDistance();
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

    private void resizeRowsUp() {
        //resize matrix to top
        int incSize = incdMatrix.length;
        if (E < 0.5 * incSize) { // if incidences less than half of incidence matrix size, reduce it
            incSize = (int) (0.5 * incSize);
            int size = incdMatrix[0].length;
            boolean[][] newIncdMatrix = new boolean[incSize][size];
            for (int j = 0; j < E; j++) {
                newIncdMatrix[j] = Arrays.copyOf(incdMatrix[j], size);
            }
            incdMatrix = newIncdMatrix;
        }
    }

} // end of class IndMatrix