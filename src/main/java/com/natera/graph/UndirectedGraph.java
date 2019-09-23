package com.natera.graph;

import com.natera.graph.holders.EdgesHolder;
import com.natera.graph.holders.UndirectedEdgesHolder;

public class UndirectedGraph<V, E> extends AbstractGraph<V, E> {

    @Override
    protected EdgesHolder<E> createEdgesHolder() {
        return new UndirectedEdgesHolder<>();
    }

}
