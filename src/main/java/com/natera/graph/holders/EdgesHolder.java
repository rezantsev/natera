package com.natera.graph.holders;

import java.util.Set;

/**
 * Abstraction to specify common behavior of edge holders.
 *
 * @param <E> The edge type
 */
public interface EdgesHolder<E> {
    void addIngoingEdge(E e);

    void addOutgoingEdge(E e);

    Set<E> getIngoingEdges();

    Set<E> getOutgoingEdges();

}
