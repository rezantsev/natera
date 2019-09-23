package com.natera.graph;

import com.natera.graph.holders.DirectedEdgesHolder;
import com.natera.graph.holders.EdgesHolder;

public class DirectedGraph<V, E> extends AbstractGraph<V, E> {

    @Override
    protected EdgesHolder<E> createEdgesHolder() {
        return new DirectedEdgesHolder<>();
    }

}
