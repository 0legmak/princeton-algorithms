import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    
    private WordNet wordnet;

    public Outcast(WordNet wordnet) {         // constructor takes a WordNet object
        this.wordnet = wordnet;
    }
    
    public String outcast(String[] nouns) {  // given an array of WordNet nouns, return an outcast
        int[][] dist = new int[nouns.length][nouns.length];
        for (int i = 0; i < nouns.length; ++i) {
            for (int j = i + 1; j < nouns.length; ++j) {
                dist[i][j] = dist[j][i] = wordnet.distance(nouns[i], nouns[j]);
            }
        }
        int[] dist_sum = new int[nouns.length];
        for (int i = 0; i < nouns.length; ++i) {
            for (int j = 0; j < nouns.length; ++j) {
                dist_sum[i] += dist[i][j];
            }
        }
        int max_val = dist_sum[0];
        int max_idx = 0;
        for (int i = 1; i < nouns.length; ++i) {
            if (max_val < dist_sum[i]) {
                max_val = dist_sum[i];
                max_idx = i;
            }
        }
        return nouns[max_idx];
    }
    
    public static void main(String[] args) { // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
