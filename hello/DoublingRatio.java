import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class DoublingRatio {
    public static double timeTrial(int N) { // Time ThreeSum.count() for N random 6-digit ints.
        Stopwatch timer = new Stopwatch();
        new PercolationStats(N, 100);
        return timer.elapsedTime();
    }
    public static void main(String[] args) {
        double prev = timeTrial(1);
        for (int N = 2; true; N += N) {
            double time = timeTrial(N);
            StdOut.printf("%6d %7.1f ", N, time);
            StdOut.printf("%5.1f\n", time/prev);
            prev = time;
        }
    }
}
