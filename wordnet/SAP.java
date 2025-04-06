import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private class QueueItem {
        public int v;
        public int dist;
        public QueueItem(int v, int dist) {
            this.v = v;
            this.dist = dist;
        }
    }

    private class Result {
        int length;
        int ancestor;
        Result(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }
    }

    private Digraph graph;

    private Result search(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        HashMap<Integer, Integer> dist1 = new HashMap<>();
        Queue<QueueItem> bfs1 = new Queue<>();
        for (Integer node : v) {
            if (node == null || node < 0 || node >= graph.V()) {
                throw new IllegalArgumentException();
            }
            bfs1.enqueue(new QueueItem(node, 0));
            dist1.put(node, 0);
        }

        HashMap<Integer, Integer> dist2 = new HashMap<>();
        Queue<QueueItem> bfs2 = new Queue<>();
        for (Integer node : w) {
            if (node == null || node < 0 || node >= graph.V()) {
                throw new IllegalArgumentException();
            }
            if (dist1.containsKey(node)) {
                return new Result(0, node);
            }
            bfs2.enqueue(new QueueItem(node, 0));
            dist2.put(node, 0);
        }
        
        int min_dist = Integer.MAX_VALUE;
        int ancestor = 0;
        int curr_dist = 0;
        while ((!bfs1.isEmpty() || !bfs2.isEmpty()) && curr_dist < min_dist) {
            while (!bfs1.isEmpty() && bfs1.peek().dist == curr_dist) {
                for (Integer next : graph.adj(bfs1.dequeue().v)) {
                    if (dist2.containsKey(next)) {
                        int dist = curr_dist + 1 + dist2.get(next);
                        if (min_dist > dist) {
                            min_dist = dist;
                            ancestor = next;
                        }
                    }
                    if (!dist1.containsKey(next)) {
                        dist1.put(next, curr_dist + 1);
                        bfs1.enqueue(new QueueItem(next, curr_dist + 1));
                    }
                }
            }
            while (!bfs2.isEmpty() && bfs2.peek().dist == curr_dist) {
                for (Integer next : graph.adj(bfs2.dequeue().v)) {
                    if (dist1.containsKey(next)) {
                        int dist = curr_dist + 1 + dist1.get(next);
                        if (min_dist > dist) {
                            min_dist = dist;
                            ancestor = next;
                        }
                    }
                    if (!dist2.containsKey(next)) {
                        dist2.put(next, curr_dist + 1);
                        bfs2.enqueue(new QueueItem(next, curr_dist + 1));
                    }
                }
            }
            ++curr_dist;
        }
        return min_dist == Integer.MAX_VALUE ? new Result(-1, -1) : new Result(min_dist, ancestor);
    }

    private Result search(int v, int w) {
        List<Integer> q1 = new ArrayList<>();
        q1.add(v);
        List<Integer> q2 = new ArrayList<>();
        q2.add(w);
        return search(q1, q2);
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return search(v, w).length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return search(v, w).ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return search(v, w).length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return search(v, w).ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
