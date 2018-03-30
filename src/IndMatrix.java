import java.io.*;
import java.util.*;


/**
 * Incidence matrix implementation for the FriendshipGraph interface.
 * 
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2016.
 */
public class IndMatrix <T extends Object> implements FriendshipGraph<T>
{

    private List<T> V; //vertices
    private List<T> E; //edges
    private boolean[][] incdMatrix;

	/**
	 * Contructs empty graph.
	 */
    IndMatrix() {
        V = new ArrayList<>();
        E = new ArrayList<>();
    	incdMatrix = new boolean[V.size() + 1][E.size() + 1];
    } // end of IndMatrix()
    
    
    public void addVertex(T vertLabel) {
        if(!V.contains(vertLabel)){ // if label doesn't exist
            V.add(vertLabel);
            int size = incdMatrix[0].length; // size matrix
            //resize matrix only if qty of v > 0.5 size
            if (V.size() > 0.5 * size) {
                size = 2 * size;
                boolean[][] newIncdMatrix = new boolean[E.size()+1][size];
                for (int i = 0; i < incdMatrix.length; i++) {
                    newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], size);
                }
                incdMatrix = newIncdMatrix;
            }
        }
    } // end of addVertex()

    public void removeVertex(T vertLabel) {
        int removeAtIndex = V.indexOf(vertLabel);
        if (removeAtIndex >= 0) {
            V.remove(vertLabel);
            // set col left
            for ( int i = E.size()-1; i >= 0; i-- ) {
                 if (incdMatrix[i][removeAtIndex]) { // found occurrence, shift row up
                     if (incdMatrix[i + 1] != null) {
                         incdMatrix[i] = incdMatrix[i + 1];
                     } else {
                         incdMatrix[i] = new boolean[incdMatrix[0].length];
                     }
                 } else { // shift column left
                    System.arraycopy(incdMatrix[i], removeAtIndex + 1,
                            incdMatrix[i], removeAtIndex, incdMatrix[i].length - removeAtIndex - 1);
                }
            }
        }
        // resize matrix
        int size = incdMatrix[0].length;
        int numV = V.size();
        if (numV < 0.5 * size) {
            size = (int) (0.5 * size);
            boolean[][] newIncdMatrix = new boolean[E.size()+1][size];
            for (int i = 0; i < incdMatrix.length; i++) {
                newIncdMatrix[i] = Arrays.copyOf(incdMatrix[i], size);
            }
            incdMatrix = newIncdMatrix;
        }
    } // end of removeVertex()
    
    private void rowsUp(){
        
    }
    
    
    public void addEdge(T srcLabel, T tarLabel) {
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0) {

        }
        // TODO addEdge
    } // end of addEdge()
	

    public ArrayList<T> neighbours(T vertLabel) {
        ArrayList<T> neighbours = new ArrayList<T>();
        
        // TODO neighbours
        
        return neighbours;
    } // end of neighbours()

    public void removeEdge(T srcLabel, T tarLabel) {
        // TODO removeEdge
    } // end of removeEdges()
	
    
    public void printVertices(PrintWriter os) {
        // TODO printVerices
    } // end of printVertices()
	
    
    public void printEdges(PrintWriter os) {
        // TODO printEdges
    } // end of printEdges()
    
    
    public int shortestPathDistance(T vertLabel1, T vertLabel2) {
    	// TODO shortestPath
    	
        // if we reach this point, source and target are disconnected
        return disconnectedDist;    	
    } // end of shortestPathDistance()
    
} // end of class IndMatrix
