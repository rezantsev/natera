package com.natera.graph;

import java.util.List;

import com.natera.graph.holders.DirectedEdgesHolder;
import com.natera.graph.holders.EdgesHolder;

public class DirectedGraph<V, E> extends AbstractGraph<V, E> {

    @Override
    protected EdgesHolder<E> createEdgesHolder() {
        return new DirectedEdgesHolder<>();
    }

}
