import java.util.HashMap;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {

    private HashMap<Integer, String> synset_map;
    private HashMap<String, Bag<Integer>> noun_map;
    private SAP sap;

   // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        noun_map = new HashMap<>();
        synset_map = new HashMap<>();
        int synset_count = 0;
        In in = new In(synsets);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            String[] parts = line.split(",");
            int synset_id = Integer.parseInt(parts[0]);
            synset_map.put(synset_id, parts[1]);
            for (String noun : parts[1].split(" ")) {
                Bag<Integer> id_list = noun_map.get(noun);
                if (id_list == null) {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(synset_id);
                    noun_map.put(noun, bag);
                } else {
                    id_list.add(synset_id);
                }
            }
            synset_count = Math.max(synset_count, synset_id + 1);
        }

        Digraph graph = new Digraph(synset_count);
        in = new In(hypernyms);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            String[] parts = line.split(",");
            int synset_id = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; ++i) {
                int hypernym_id = Integer.parseInt(parts[i]);
                graph.addEdge(synset_id, hypernym_id);
            }
        }

        DirectedCycle cycle_detect = new DirectedCycle(graph);
        if (cycle_detect.hasCycle()) {
            throw new IllegalArgumentException("Graph is not a DAG");
        }
        int cnt = 0;
        for (int node = 0; node < graph.V(); ++node) {
            if (graph.outdegree(node) == 0) {
                ++cnt;
            }
        }
        if (cnt != 1) {
            throw new IllegalArgumentException("Graph is not a rooted DAG");
        }

        sap = new SAP(graph);

        // StdOut.println("nouns = " + noun_map.size());
        // StdOut.println("V() = " + graph.V());
        // StdOut.println("E() = " + graph.E());
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return noun_map.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return noun_map.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        Bag<Integer> nodes1 = noun_map.get(nounA);
        Bag<Integer> nodes2 = noun_map.get(nounB);
        if (nodes1 == null || nodes2 == null) {
            throw new IllegalArgumentException();
        }
        return sap.length(nodes1, nodes2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        Bag<Integer> nodes1 = noun_map.get(nounA);
        Bag<Integer> nodes2 = noun_map.get(nounB);
        if (nodes1 == null || nodes2 == null) {
            throw new IllegalArgumentException();
        }
        int ancestor = sap.ancestor(nodes1, nodes2);
        return synset_map.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet word_net = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(word_net.distance("individual", "edible_fruit"));
        StdOut.println(word_net.sap("individual", "edible_fruit"));
    }
}
