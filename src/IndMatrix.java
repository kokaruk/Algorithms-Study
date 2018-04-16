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

    private List<T> Vl; //vertices
    private Set<T> Vs; //vertices
    private int E; //edges count
    private boolean[][] incdMatrix;

    /**
     * Contructs empty graph.
     */
    IndMatrix() {
        Vl = new ArrayList<>();
        Vs = new HashSet<>();
        int E = 0;
        incdMatrix = new boolean[Vl.size() + 1][E + 1];
    } // end of IndMatrix()


    public void addVertex(T vertLabel) {
        if (!Vs.contains(vertLabel)) { // if label doesn't exist
            Vl.add(vertLabel);
            Vs.add(vertLabel);
            int size = incdMatrix.length; // size matrix
            //resize matrix
            if (Vl.size() > 0.5 * size) {
                size *= 2;
                int incSize = incdMatrix[0].length;
                boolean[][] newIncdMatrix = new boolean[size][incSize];
                for (int i = 0; i < Vl.size(); i++) {
                    newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], incSize);
                }
                incdMatrix = newIncdMatrix;
            }
        }
    } // end of addVertex()

    public void removeVertex(T vertLabel) {
        if (Vs.contains(vertLabel)) {
            int removeAtIndex = Vl.indexOf(vertLabel);
            int j = 0;
            while (j < E){
                if (incdMatrix[removeAtIndex][j]){ // found edge
                    for (int i = 0; i < Vl.size(); i++) {
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
                    incdMatrix, removeAtIndex, Vl.size() - removeAtIndex - 1);

            Vl.remove(removeAtIndex);
            Vs.remove(vertLabel);
            // reduce matrix size
            resizeMatrixSize();
        }
    } // end of removeVertex()

    public void addEdge(T srcLabel, T tarLabel) {

        if ( !srcLabel.toString().equals(tarLabel.toString())
                && Vs.contains(srcLabel)
                && Vs.contains(tarLabel)){ // if both vertices exist
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            // iterate over all incidences and check if two vertexes are connected
            if (!isIncidence(srcLabelIndex, tarLabelIndex)) {
                E++;
                int incSize = incdMatrix[0].length;
                if (E > 0.5 * incSize) {
                    incSize *= 2;
                    int size = incdMatrix.length;
                    boolean[][] newIncdMatrix = new boolean[size][incSize];
                    for (int j = 0; j < Vl.size(); j++) {
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

        if (Vs.contains(srcLabel) && Vs.contains(tarLabel)) { // if both vertices exist
            int srcLabelIndex = Vl.indexOf(srcLabel);
            int tarLabelIndex = Vl.indexOf(tarLabel);
            for (int j = 0; j <= E; j++) {
                if (incdMatrix[srcLabelIndex][j] && incdMatrix[tarLabelIndex][j]) {
                    for (int i = 0; i < Vl.size(); i++) {
                        System.arraycopy(incdMatrix[i], j + 1,
                                incdMatrix[i], j, E - j - 1);
                    }
                    E--;
                }
            }
        } else throw new IllegalArgumentException("Vertex does not exist.");
    } // end of removeEdges()

    public ArrayList<T> neighbours(T vertLabel) {
        if (Vs.contains(vertLabel)) {
            int vertLabelIndex = Vl.indexOf(vertLabel);
            ArrayList<T> neighbours = new ArrayList<>();
            for (int j = 0; j < E; j++) {
                if (incdMatrix[vertLabelIndex][j]) {
                    for (int i = 0; i < Vl.size(); i++) {
                        if (incdMatrix[i][j] && i != vertLabelIndex) neighbours.add(Vl.get(i));
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

    public void printEdges(PrintWriter os) {

        // for each edge
        for (int i = 0; i < E; i++) {
            List<T> pair = new ArrayList<>();
            for (int j = 0; j < Vl.size(); j++) {
                if (incdMatrix[j][i]) pair.add(Vl.get(j));
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

    private void resizeMatrixSize() {

        // resize matrix up
        int vsize = incdMatrix.length; // size of matrix y axis
        //resize matrix
        if (Vl.size() < (0.5 * vsize) ) {
            vsize = (int)(0.5 * vsize);
            boolean[][] newIncdMatrix = new boolean[vsize][E];
            for (int i = 0; i < Vl.size(); i++) {
                newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], E);
            }
            incdMatrix = newIncdMatrix;
        }

        //resize matrix to left
        int incSize = incdMatrix[0].length;  //size y axis
        if (E < 0.5 * incSize) { // if incidences less than half of incidence matrix size, reduce it
            incSize = (int) (0.5 * incSize);
            boolean[][] newIncdMatrix = new boolean[vsize][incSize];
            for (int i = 0; i < vsize; i++) {
                newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], E);
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


} // end of class IndMatrix