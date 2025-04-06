import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double meanVal;
    private double stddevVal;
    private double confidenceLoVal;
    private double confidenceHiVal;

    private class SiteCoords {
        public int row;
        public int col;
        public SiteCoords(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        double[] stats = new double[trials];
        for (int t = 0; t < trials; ++t) {
            Percolation perc = new Percolation(n);
            SiteCoords[] siteCoords = new SiteCoords[n * n];
            int idx = 0;
            for (int row = 1; row <= n; ++row) {
                for (int col = 1; col <= n; ++col) {
                    siteCoords[idx] = new SiteCoords(row, col);
                    ++idx;
                }
            }
            StdRandom.shuffle(siteCoords);
            for (int i = 0; i < n * n; ++i) {
                perc.open(siteCoords[i].row, siteCoords[i].col);
                if (perc.percolates()) {
                    stats[t] = 1.0 * perc.numberOfOpenSites() / n / n;
                    break;
                }
            }
        }
        meanVal = StdStats.mean(stats);
        stddevVal = StdStats.stddev(stats);
        confidenceLoVal = meanVal - 1.96 * stddevVal / Math.sqrt(trials);
        confidenceHiVal = meanVal + 1.96 * stddevVal / Math.sqrt(trials);
    }

    // sample mean of percolation threshold
    public double mean() {
        return meanVal;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddevVal;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLoVal;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHiVal;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats percStats = new PercolationStats(n, trials);
        StdOut.println("mean                    = " + percStats.mean());
        StdOut.println("stddev                  = " + percStats.stddev());
        StdOut.println("95% confidence interval = [" + percStats.confidenceLo() + ", " + percStats.confidenceHi() + "]");
    }
}
