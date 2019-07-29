package com.natera.graph.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.natera.graph.ConcurrentGraphWrapper;
import com.natera.graph.DirectedGraph;
import com.natera.graph.Graph;
import com.natera.graph.UndirectedGraph;

import junit.framework.TestCase;

public class GraphTest extends TestCase {
    final static Logger logger = Logger.getLogger(GraphTest.class);

    public void testSearchPath() {
        searchPaths(new UndirectedGraph<Vertex, Edge>());
        searchPaths(new DirectedGraph<Vertex, Edge>());
    }

    public void testSearchCycledPaths() {
        searchPathsInCyclic(new UndirectedGraph<Vertex, Edge>());
        searchPathsInCyclic(new DirectedGraph<Vertex, Edge>());
    }

    /**
     * Checks if swapping parameters in getPath(v1, v2) still returns result for UndirectedGraph,
     * but don't for DirectedGraph
     */
    public void testSwappedVerticesSearch() {
        UndirectedGraph<Vertex, Edge> uGraph = new UndirectedGraph<>();
        DirectedGraph<Vertex, Edge> dGraph = new DirectedGraph<>();

        Vertex v1 = new Vertex("1");
        uGraph.addVertex(v1);
        dGraph.addVertex(v1);
        Vertex v11 = new Vertex("1.1");
        uGraph.addVertex(v11);
        dGraph.addVertex(v11);
        Vertex v111 = new Vertex("1.1.1");
        uGraph.addVertex(v111);
        dGraph.addVertex(v111);

        Edge e11 = new Edge(v1.getName() + " -> " + v11.getName());
        Edge e111 = new Edge(v11.getName() + " -> " + v111.getName());
        uGraph.addEdge(v1, v11, e11);
        uGraph.addEdge(v11, v111, e111);
        dGraph.addEdge(v1, v11, e11);
        dGraph.addEdge(v11, v111, e111);

        List<Edge> path;

        path = dGraph.getPath(v1, v111);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(e11, path.get(0));
        assertEquals(e111, path.get(1));

        path = dGraph.getPath(v111, v1);
        assertNull(path);

        path = uGraph.getPath(v1, v111);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(e11, path.get(0));
        assertEquals(e111, path.get(1));

        path = uGraph.getPath(v111, v1);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(e111, path.get(0));
        assertEquals(e11, path.get(1));
    }

    public void testAddVertexMethod() {
        Vertex v1 = new Vertex("1");
        UndirectedGraph<Vertex, Edge> uGraph = new UndirectedGraph<>();

        // should add a new vertex
        boolean isAdded = uGraph.addVertex(v1);
        assertTrue("Vertex should be added", isAdded);

        // should not be added the second time
        isAdded = uGraph.addVertex(v1);
        assertFalse("Vertex should not be added", isAdded);

        isAdded = uGraph.addVertex(null);
        assertFalse("Null vertex should not be added", isAdded);
    }

    public void testAddEdgeMethod() {
        Vertex v1 = new Vertex("1");
        Vertex v2 = new Vertex("2");

        UndirectedGraph<Vertex, Edge> uGraph = new UndirectedGraph<>();
        Edge edge = new Edge("orphan");

        // should not insert edge to vertices which are not yet in the graph
        boolean isAdded = uGraph.addEdge(v1, v2, edge);
        assertFalse("Edge should not be added", isAdded);

        uGraph.addVertex(v1);
        uGraph.addVertex(v2);

        // should insert edge now
        isAdded = uGraph.addEdge(v1, v2, edge);
        assertTrue("Edge should be added", isAdded);

        // should not insert already inserted edge
        isAdded = uGraph.addEdge(v1, v2, edge);
        assertFalse("Edge should not be added", isAdded);

        // should not insert edge to null vertices
        isAdded = uGraph.addEdge(null, null, new Edge("orphan-2"));
        assertFalse("Edge should not be added to a null vertex", isAdded);
    }

    public void testTraverseMethod() {
        VertexCounter undirectConsumer = new VertexCounter();
        VertexCounter directConsumer = new VertexCounter();
        traverseGraph(new UndirectedGraph<Vertex, Edge>(), undirectConsumer::accept);
        traverseGraph(new DirectedGraph<Vertex, Edge>(), directConsumer::accept);

        assertEquals(7, undirectConsumer.getCount());
        assertEquals(3, directConsumer.getCount());
    }

    /**
     * Checks if the search algorithm does not go to stack overflow in case of cyclic graph
     */
    private void searchPathsInCyclic(Graph<Vertex, Edge> graph) {
        Vertex v1 = new Vertex("1");
        graph.addVertex(v1);
        Vertex v11 = new Vertex("1.1");
        graph.addVertex(v11);
        Vertex v111 = new Vertex("1.1.1");
        graph.addVertex(v111);

        Edge e11 = new Edge(v1.getName() + " -> " + v11.getName());
        Edge e111 = new Edge(v11.getName() + " -> " + v111.getName());
        Edge cycleEdge = new Edge(v111.getName() + " -> " + v1.getName());

        graph.addEdge(v1, v11, e11);
        graph.addEdge(v11, v111, e111);
        graph.addEdge(v111, v1, cycleEdge);

        List<Edge> path;
        Vertex v = new Vertex("non-existent-vertex");
        path = graph.getPath(v1, v);
        assertNull(path);
        path = graph.getPath(v, v1);
        assertNull(path);
    }

    private void searchPaths(Graph<Vertex, Edge> graph) {
        Vertex v1 = new Vertex("1");
        graph.addVertex(v1);
        Vertex v11 = new Vertex("1.1");
        graph.addVertex(v11);
        Vertex v111 = new Vertex("1.1.1");
        graph.addVertex(v111);
        Vertex v112 = new Vertex("1.1.2");
        graph.addVertex(v112);
        Vertex v12 = new Vertex("1.2");
        graph.addVertex(v12);
        Vertex v121 = new Vertex("1.2.1");
        graph.addVertex(v121);
        Vertex v122 = new Vertex("1.2.2");
        graph.addVertex(v122);

        Edge e11 = new Edge(v1.getName() + " -> " + v11.getName());
        Edge e12 = new Edge(v1.getName() + " -> " + v12.getName());
        Edge e111 = new Edge(v11.getName() + " -> " + v111.getName());
        Edge e112 = new Edge(v11.getName() + " -> " + v112.getName());
        Edge e121 = new Edge(v12.getName() + " -> " + v121.getName());
        Edge e122 = new Edge(v12.getName() + " -> " + v122.getName());

        graph.addEdge(v1, v11, e11);
        graph.addEdge(v1, v12, e12);
        graph.addEdge(v11, v111, e111);
        graph.addEdge(v11, v112, e112);
        graph.addEdge(v12, v121, e121);
        graph.addEdge(v12, v122, e122);

        List<Edge> path;

        path = graph.getPath(v1, v11);
        assertNotNull(path);
        assertEquals(1, path.size());
        assertEquals(e11, path.get(0));

        path = graph.getPath(v1, v12);
        assertNotNull(path);
        assertEquals(1, path.size());
        assertEquals(e12, path.get(0));

        path = graph.getPath(v1, v111);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(e11, path.get(0));
        assertEquals(e111, path.get(1));

        path = graph.getPath(v1, v112);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(e11, path.get(0));
        assertEquals(e112, path.get(1));

        path = graph.getPath(v12, v122);
        assertNotNull(path);
        assertEquals(1, path.size());
        assertEquals(e122, path.get(0));

        path = graph.getPath(null, null);
        assertNull(path);
    }

    private void traverseGraph(Graph<Vertex, Edge> graph, Consumer<Vertex> consumer) {
        Vertex v1 = new Vertex("1");
        graph.addVertex(v1);
        Vertex v11 = new Vertex("1.1");
        graph.addVertex(v11);
        Vertex v111 = new Vertex("1.1.1");
        graph.addVertex(v111);
        Vertex v112 = new Vertex("1.1.2");
        graph.addVertex(v112);
        Vertex v12 = new Vertex("1.2");
        graph.addVertex(v12);
        Vertex v121 = new Vertex("1.2.1");
        graph.addVertex(v121);
        Vertex v122 = new Vertex("1.2.2");
        graph.addVertex(v122);

        Edge e11 = new Edge(v1.getName() + " -> " + v11.getName());
        Edge e12 = new Edge(v1.getName() + " -> " + v12.getName());
        Edge e111 = new Edge(v11.getName() + " -> " + v111.getName());
        Edge e112 = new Edge(v11.getName() + " -> " + v112.getName());
        Edge e121 = new Edge(v12.getName() + " -> " + v121.getName());
        Edge e122 = new Edge(v12.getName() + " -> " + v122.getName());

        graph.addEdge(v1, v11, e11);
        graph.addEdge(v1, v12, e12);
        graph.addEdge(v11, v111, e111);
        graph.addEdge(v11, v112, e112);
        graph.addEdge(v12, v121, e121);
        graph.addEdge(v12, v122, e122);

        graph.traverse(v12, consumer);
    }

    /**
     * Several threads are trying to read graph while it is in filling process (writing thread).
     * Finally, all of read task should successfully obtain path from first to the last vertex.
     */
    public void testConcurrency() throws InterruptedException {
        Graph<Vertex, Edge> graph = new ConcurrentGraphWrapper<>(new UndirectedGraph<>(), false);
        Vertex v1 = new Vertex("start");
        Vertex v2 = new Vertex("end");

        // add some reading threads and one writing thread
        final int numOfReadThreads = 3;
        final int numOfWriteThreads = 1;
        CountDownLatch latch = new CountDownLatch(numOfReadThreads + numOfWriteThreads);
        List<GraphThread> threads = new ArrayList<>();
        for (int i = 0; i < numOfReadThreads; i++) {
            threads.add(new ReadGraphThread("read-thread-" + i, graph, v1, v2, latch));
        }
        for (int i = 0; i < numOfWriteThreads; i++) {
            threads.add(new WriteGraphThread("write-thread-" + i, graph, v1, v2, latch));
        }

        Executor executor = Executors.newFixedThreadPool(threads.size());
        for (Runnable runnable : threads) {
            executor.execute(runnable);
        }

        latch.await(10, TimeUnit.SECONDS);

        for (GraphThread graphThread : threads) {
            assertTrue(graphThread.getName() + " should exit succesfully", graphThread.isSuccess());
        }
    }

    /**
     * Just calculates the amount of visited nodes.
     */
    private static class VertexCounter implements Consumer<Vertex> {
        private int count;

        @Override
        public void accept(Vertex v) {
            count++;
            logger.debug(String.format("Visited vertex: %s", v));
        }

        public int getCount() {
            return count;
        }
    }

    private static abstract class GraphThread implements Runnable {
        protected String name;
        protected Graph<Vertex, Edge> graph;
        protected Vertex v1;
        protected Vertex v2;
        protected CountDownLatch latch;
        private boolean success;

        public GraphThread(String name, Graph<Vertex, Edge> graph, Vertex v1, Vertex v2, CountDownLatch latch) {
            this.name = name;
            this.graph = graph;
            this.v1 = v1;
            this.v2 = v2;
            this.latch = latch;
        }

        public String getName() {
            return name;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    private static class ReadGraphThread extends GraphThread {

        public ReadGraphThread(String name, Graph<Vertex, Edge> graph, Vertex v1, Vertex v2, CountDownLatch latch) {
            super(name, graph, v1, v2, latch);
        }

        @Override
        public void run() {
            try {
                logger.debug(String.format("Reading thread has been started: %s", name));
                while (graph.getPath(v1, v2) == null) {
                    // exit if path is found
                }
                logger.debug(String.format("Reading thread has been exited: %s", name));
                setSuccess(true);
            } catch (Exception e) {
                logger.error("Reading graph error", e);
            } finally {
                latch.countDown();
            }
        }
    }

    private static class WriteGraphThread extends GraphThread {
        private final int pathLength = 10;

        public WriteGraphThread(String name, Graph<Vertex, Edge> graph, Vertex v1, Vertex v2, CountDownLatch latch) {
            super(name, graph, v1, v2, latch);
        }

        @Override
        public void run() {
            try {
                logger.debug(String.format("Writing started"));

                graph.addVertex(v1);
                Vertex current = v1;
                logger.debug(String.format("Insert vertex: %s", v1));
                for (int i = 0; i < pathLength; i++) {
                    Vertex v = new Vertex("v_" + i);
                    graph.addVertex(v);
                    Edge e = new Edge(current + " -> " + v);
                    graph.addEdge(current, v, e);
                    logger.debug(String.format("Insert vertex: %s [%s]", v, e));
                    current = v;
                    Thread.sleep(100);
                }
                graph.addVertex(v2);
                Edge e = new Edge(current + " -> " + v2);
                graph.addEdge(current, v2, e);
                logger.debug(String.format("Insert vertex: %s [%s]", v2, e));

                logger.debug(String.format("Writing finished"));
                setSuccess(true);
            } catch (InterruptedException e) {
                logger.error("writing thread was interrupted", e);
            } finally {
                latch.countDown();
            }
        }
    }
}
