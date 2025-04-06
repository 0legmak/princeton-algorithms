import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        int i = 0;
        for (; i < k; ++i) {
            String str = StdIn.readString();
            queue.enqueue(str);
        }
        for (;!StdIn.isEmpty(); ++i) {
            String str = StdIn.readString();
            if (StdRandom.uniformInt(i + 1) < k) {
                queue.dequeue();
                queue.enqueue(str);
            }
        }
        for (String str : queue) {
            StdOut.println(str);
        }
    }
}
