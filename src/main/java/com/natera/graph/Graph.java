package com.natera.graph;

import java.util.List;
import java.util.function.Consumer;

/**
 * Graph with specified by the user vertices and edges types.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public interface Graph<V, E> {
	/**
	 * Adds vertex to the graph.
	 * 
	 * @param v Vertex to be added
	 * @return true if vertex was added
	 */
	boolean addVertex(V v);

	/**
	 * Adds the specified edge to the graph. Vertices should be already in the
	 * graph.
	 * 
	 * @param src Source vertex
	 * @param dst Destination vertex
	 * @param e   Edge to be added
	 * @return true if edge was added
	 */
	boolean addEdge(V src, V dst, E e);

	/**
	 * Finds path between two vertices
	 * 
	 * @param v1 the vertex to start with
	 * @param v2 the target vertex
	 * @return List of edges between the specified vertices
	 */
	List<E> getPath(V v1, V v2);

	/**
	 * Traverse through all of the graph vertices, passing them to the specified
	 * consumer.
	 *
	 * @param start    Vertex to start with or random if it is not specified
	 * @param consumer User defined function to accept traversed vertices
	 */
	void traverse(V start, Consumer<? super V> consumer);
}
