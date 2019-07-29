package com.natera.graph;

import java.util.List;

import com.natera.graph.holders.EdgesHolder;
import com.natera.graph.holders.UndirectedEdgesHolder;

public class UndirectedGraph<V, E> extends AbstractGraph<V, E> {

    @Override
    protected EdgesHolder<E> createEdgesHolder() {
        return new UndirectedEdgesHolder<>();
    }

}
