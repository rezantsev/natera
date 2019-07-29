package com.natera.graph.holders;

import java.util.HashSet;
import java.util.Set;

/**
 * This holder keeps both ingoing and outgoing edges in the same collection.
 *
 * @param <E> The edge type
 */
public class UndirectedEdgesHolder<E> implements EdgesHolder<E> {
    private Set<E> edges = new HashSet<>();

    @Override
    public void addOutgoingEdge(E e) {
        edges.add(e);
    }

    @Override
    public void addIngoingEdge(E e) {
        edges.add(e);
    }

    @Override
    public Set<E> getIngoingEdges() {
        return edges;
    }

    @Override
    public Set<E> getOutgoingEdges() {
        return edges;
    }
}
