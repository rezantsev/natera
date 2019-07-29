package com.natera.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.natera.graph.holders.EdgesHolder;

public abstract class AbstractGraph<V, E> implements Graph<V, E> {
	final static Logger logger = Logger.getLogger(AbstractGraph.class);

	protected Map<V, EdgesHolder<E>> vertices = new HashMap<>();
	protected Map<E, EdgeMetaData> edges = new HashMap<>();

	/**
	 * Associates given vertex with a new instance of EdgesHolder.
	 * 
	 * @return true if the edge was actually added
	 * @see EdgesHolder
	 */
	public boolean addVertex(V v) {
		if (v == null) {
			return false;
		}
		if (vertices.containsKey(v)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Vertex is already in the graph: %s", v));
			}
			return false;
		}
		EdgesHolder<E> holder = createEdgesHolder();
		vertices.put(v, holder);
		return true;
	}

	/**
	 * Adds edge between the specified vertices. The method expects what vertices
	 * are already in graph.
	 *
	 * @return true if the edge was added
	 */
	public boolean addEdge(V src, V dst, E e) {
		if (e == null) {
			return false;
		}
		if (edges.containsKey(e)) {
			if (logger.isEnabledFor(Level.WARN)) {
				logger.warn(String.format("An attempt to add already inserted edge \"%s\"", e));
			}
			return false;
		}
		if (!vertices.containsKey(src) || !vertices.containsKey(dst)) {
			if (logger.isEnabledFor(Level.WARN)) {
				logger.warn(String.format("An attempt to add edge \"%s\" to a not existent vertex", e));
			}
			return false;
		}
		EdgesHolder<E> srcHolder = vertices.get(src);
		srcHolder.addOutgoingEdge(e);
		EdgesHolder<E> dstHolder = vertices.get(dst);
		dstHolder.addIngoingEdge(e);
		edges.put(e, new EdgeMetaData(src, dst));
		return true;
	}

	/**
	 * Finds path between two vertices. Returns null if path is not found.
	 */
	public List<E> getPath(V v1, V v2) {
		List<E> path = new ArrayList<>();
		Set<V> seenVertices = new HashSet<>();
		if (searchPath(v1, v2, seenVertices, path)) {
			return path;
		}
		return null;
	}

	/**
	 * Depth first implementation of traverse algorithm. Invokes
	 * {@link java.util.function.Consumer#accept(Object)} method for every visited
	 * vertex.
	 * 
	 * @param start    Vertex to start with or random if it is not specified
	 * @param consumer User defined function to accept traversed vertices
	 */
	public void traverse(V start, Consumer<? super V> consumer) {
		if (vertices.isEmpty()) {
			return;
		}
		if (start == null) {
			// choose a random vertex
			start = vertices.keySet().iterator().next();
		}
		if (!vertices.containsKey(start)) {
			if (logger.isEnabledFor(Level.WARN)) {
				logger.warn(String.format("Not existent vertex was specfied: %s", start));
			}
			return;
		}
		doTraverse(start, consumer, new HashSet<V>());
	}

	/**
	 * Recursively walks through the graph.
	 * @param v Vertex to start with
	 * @param consumer The user defined function to accept vertices
	 * @param seenVertices The set of already visited vertices
	 */
	protected void doTraverse(V v, Consumer<? super V> consumer, Set<V> seenVertices) {
		consumer.accept(v);
		seenVertices.add(v);
		EdgesHolder<E> holder = vertices.get(v);
		if (holder == null) {
			return;
		}
		Set<E> outEdges = holder.getOutgoingEdges();
		if (outEdges == null) {
			return;
		}
		for (E edge : outEdges) {
			EdgeMetaData edgeData = edges.get(edge);
			// get opposite vertex
			V nextVertex = v.equals(edgeData.getDst()) ? edgeData.getSrc() : edgeData.getDst();
			if (seenVertices.contains(nextVertex)) {
				continue;
			}
			doTraverse(nextVertex, consumer, seenVertices);
		}
	}

	/**
	 * Recursively searches path from current to the target vertex.
	 *
	 * @param current Currently verified vertex
	 * @param target  The target vertex to be searched
	 * @param seenVertices The set of already visited vertices
	 * @param path    List of edges from current to target vertex
	 * @return true if the path is found
	 */
	protected boolean searchPath(V current, V target, Set<V> seenVertices, List<E> path) {
		seenVertices.add(current);
		EdgesHolder<E> holder = vertices.get(current);
		if (holder == null) {
			return false;
		}
		Set<E> outEdges = holder.getOutgoingEdges();
		if (outEdges == null) {
			return false;
		}
		for (E edge : outEdges) {
			EdgeMetaData edgeData = edges.get(edge);
			// get opposite vertex
			V nextVertex = current.equals(edgeData.getDst()) ? edgeData.getSrc() : edgeData.getDst();
			if (seenVertices.contains(nextVertex)) {
				continue;
			}
			if (nextVertex.equals(target) || searchPath(nextVertex, target, seenVertices, path)) {
				path.add(0, edge);
				return true;
			}
		}
		return false;
	}

	/**
	 * Factory method to create the EdgesHolder structure for vertex edges.
	 * Ancestors should redefine this method to provide holder of the desired type.
	 * 
	 * @return New instance of EdgesHolder
	 * @see com.natera.graph.holders.UndirectedEdgesHolder
	 * @see com.natera.graph.holders.DirectedEdgesHolder
	 */
	protected abstract EdgesHolder<E> createEdgesHolder();

	/**
	 * Structure to keep some helpful data about the Edge.
	 */
	protected class EdgeMetaData {
		private V src;
		private V dst;

		public EdgeMetaData(V src, V dst) {
			this.setSrc(src);
			this.setDst(dst);
		}

		public V getSrc() {
			return src;
		}

		public void setSrc(V src) {
			this.src = src;
		}

		public V getDst() {
			return dst;
		}

		public void setDst(V dst) {
			this.dst = dst;
		}

		@Override
		public String toString() {
			return src + " -> " + dst;
		}
	}

}
