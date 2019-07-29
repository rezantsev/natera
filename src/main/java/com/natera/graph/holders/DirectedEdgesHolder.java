package com.natera.graph.holders;

import java.util.HashSet;
import java.util.Set;

/**
 * This holder keeps ingoing and outgoing edges separately.
 *
 * @param <E> The edge type
 */
public class DirectedEdgesHolder<E> implements EdgesHolder<E> {
    private Set<E> inEdges = new HashSet<>();
    private Set<E> outEdges = new HashSet<>();

    @Override
    public void addOutgoingEdge(E e) {
        outEdges.add(e);
    }

    @Override
    public void addIngoingEdge(E e) {
        inEdges.add(e);
    }

    @Override
    public Set<E> getIngoingEdges() {
        return inEdges;
    }

    @Override
    public Set<E> getOutgoingEdges() {
        return outEdges;
    }
}
