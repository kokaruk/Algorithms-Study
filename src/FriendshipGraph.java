import java.io.*;
import java.util.*;

/**
 * Friendship graph interface. 
 *
 * Note, you should not need to modify this.
 *
 * @author Jeffrey Chan, 2016.
 */
public interface FriendshipGraph<T extends Object>
{
    /** Distance value for source and target vertex pairs that are disconnected. */
    public static final int disconnectedDist = -1;

	/**
	 * Adds a vertex to the graph.  If the vertex already exists in the graph, no changes are made.
	 *
	 * @param vertLabel Vertex to add.
	 */
	public abstract void addVertex(T vertLabel);
	
	
	/**
	 * Adds an edge to the graph.  If the edge already exists in the graph, no changes are made.  If one of the vertices doesn't exist, a warning to System.err should be issued 
	 * and no edge or vertices should be added.
	 *
	 * @param srcLabel Source vertex of edge to add.
	 * @param tarLabel Target vertex of edge to add.
	 */
	public abstract void addEdge(T srcLabel, T tarLabel);
        
        
    /**
     * Returns the set of neighbours for a vertex.  If vertex doesn't exist, then a warning to System.err should be issued.
     *
     * @param vertLabel Vertex to find the neighbourhood for.
     *
     * @returns The set of neighbours of vertex 'vertLabel'.
     */
    public abstract ArrayList<T> neighbours(T vertLabel);
	
	
	/**
	 * Removes a vertex from the graph.  If the vertex doesn't exists in the graph, no changes are made, but a warning to System.err should be issued.
	 *
	 * @param vertLabel Vertex to remove.
	 */	
	public abstract void removeVertex(T vertLabel);
	
	
	/**
	 * Removes an edge from the graph.  If the edge doesn't exists in the graph, no changes are made, but a warning to System.err should be issued.  If one of the 
	 * vertices doesn't exist, a warning to System.err should be issued and no edge or vertices should be added.
	 *
	 * @param srcLabel Source vertex of edge to remove.
	 * @param tarLabel Target vertex of edge to remove.
	 */	
	public abstract void removeEdge(T srcLabel, T tarLabel);
	
	
	/**
	 * Prints the list of vertices to PrintWriter 'os'.
	 *
	 * @param os PrinterWriter to print to.
	 */
	public abstract void printVertices(PrintWriter os);
	
	
	/**
	 * Prints the list of edges to PrintWriter 'os'.
	 *
	 * @param os PrinterWriter to print to.
	 */	
	public abstract void printEdges(PrintWriter os);
	
	
	/**
	 * Computes the shortest path distance between two vertices.  If one or both of the vertices doesn't exist, then a warning to System.err should be issued.
	 *
	 * Note for undirected graph, which vertex is the origin and which is the destination doesn't matter, i.e., distance(A,B) = distance (B,A).
	 *
	 * @param vertLabel1 Origin vertex to compute shortest path distance from.  
	 * @param vertLabel2 Destination vertex to compute shortest path distance to.
	 *
	 * @returns Shortest path distance.  If there is no path between the vertices, then the value of FriendshipGraph.disconnectedDist should be returned.
	 */
	public abstract int shortestPathDistance(T vertLabel1, T vertLabel2);
	
} // end of interface FriendshipGraph