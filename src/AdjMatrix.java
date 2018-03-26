import java.io.*;
import java.util.*;


/**
 * Adjacency matrix implementation for the FriendshipGraph interface.
 * 
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2016.
 */
public class AdjMatrix <T extends Object> implements FriendshipGraph<T>
{

    private List<T> V;  // vertices
    private boolean[][] adjMatrix;
    int E; //edges

	/**
	 * Constructs empty graph.
	 */
    public AdjMatrix() {
    	// Implement me!
        V = new LinkedList<>();
        E = 0; // edges count
        adjMatrix = new boolean[V.size()+1][V.size()+1];

    } // end of AdjMatrix()
    

    /**
     *  A better way of doing this would be to anticipate the addition of more vertices
     *  and create a new array of 2*|V| size
     *  every time we fill up more than 1/2 the current array.
     *  This will limit the number of instances where we have to resize our matrix.
     */
    public void addVertex(T vertLabel) {
        // Implement me!
        // If the number of vertices is more than half the size of matrix,
        // double the size of matrix
        if(!V.contains(vertLabel)){ //if not contains
            V.add(vertLabel);
            int size = adjMatrix[0].length; // get size of matrix
            int numV = V.size();
            if (numV > 0.5 * size) {
                size = 2*size;
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
        V.remove(vertLabel);
        if(removeAtIndex >=0) {
            // shift row up
            for (int i = 0; i < V.size(); i++) {
                if(i >= removeAtIndex) {
                    if ( adjMatrix[i+1] != null) {
                        adjMatrix[i] = adjMatrix[i+1];
                    } else {
                        adjMatrix[i] = new boolean[adjMatrix[0].length];
                    }
              }
              // sift column left
              System.arraycopy(adjMatrix[i], removeAtIndex+1,
                                    adjMatrix[i], removeAtIndex, adjMatrix[i].length-removeAtIndex-1);
            }

            // resize matrix
            int size = adjMatrix[0].length;
            int numV = V.size();
            if (size < 0.5 * numV) {
                size = (int) (0.5 * size);
                boolean[][] newAdjMatrix = new boolean[size][size];
                for (int i = 0; i < size; i++) {
                    newAdjMatrix[i] = Arrays.copyOf(adjMatrix[i], size);
                }
                adjMatrix = newAdjMatrix;
            }

        }

        // Implement me!
    } // end of removeVertex()
    
    public void addEdge(T srcLabel, T tarLabel) {
        // Implement me!
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0 ){
            adjMatrix[srcLabelIndex][tarLabelIndex] = true;
            adjMatrix[tarLabelIndex][srcLabelIndex] = true;
        } else throw new NoSuchElementException("Vertex does not exist.");
    } // end of addEdge()

    public void removeEdge(T srcLabel, T tarLabel) {
        // Implement me!
        int srcLabelIndex = V.indexOf(srcLabel);
        int tarLabelIndex = V.indexOf(tarLabel);
        if (srcLabelIndex >= 0 && tarLabelIndex >= 0 ){
            adjMatrix[srcLabelIndex][tarLabelIndex] = false;
            adjMatrix[tarLabelIndex][srcLabelIndex] = false;
        } else throw new NoSuchElementException("Vertex does not exist.");
    } // end of removeEdges()

    //TODO Neighbours
    public ArrayList<T> neighbours(T vertLabel) {
        ArrayList<T> neighbours = new ArrayList<>();
        
        // Implement me!
        
        return neighbours;
    } // end of neighbours()

    
    public void printVertices(PrintWriter os) {
        // Implement me!
        Iterator<T> it = V.iterator();
        T vertice;
        if(it.hasNext()){
            vertice = it.next();
            os.print(vertice.toString());
        }
        while (it.hasNext()){
            vertice = it.next();
            os.print(" " + vertice);
        }
        os.println();
    } // end of printVertices()
	
    //TODO print edges
    public void printEdges(PrintWriter os) {
        // Implement me!

    } // end of printEdges()
    
    //TODO shortest path distance
    public int shortestPathDistance(T vertLabel1, T vertLabel2) {
    	// Implement me!
    	
        // if we reach this point, source and target are disconnected
        return disconnectedDist;    	
    } // end of shortestPathDistance()
    
} // end of class AdjMatrix