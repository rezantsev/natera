package com.natera.graph;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Allows to create thread safe versions of Graph.
 */
public class ConcurrentGraphWrapper<V, E> implements Graph<V, E> {

    private Graph<V, E> graph;
    private final ReentrantReadWriteLock readWriteLock;

    public ConcurrentGraphWrapper(Graph<V, E> graph, boolean fair) {
        this.graph = graph;
        readWriteLock = new ReentrantReadWriteLock(fair);
    }

    @Override
    public boolean addVertex(V v) {
        readWriteLock.writeLock().lock();
        try {
            return graph.addVertex(v);
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public boolean addEdge(V src, V dst, E e) {
        readWriteLock.writeLock().lock();
        try {
            return graph.addEdge(src, dst, e);
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public List<E> getPath(V v1, V v2) {
        readWriteLock.readLock().lock();
        try {
            return graph.getPath(v1, v2);
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    @Override
    public void traverse(V start, Consumer<? super V> consumer) {
        readWriteLock.readLock().lock();
        try {
            graph.traverse(start, consumer);
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

}
