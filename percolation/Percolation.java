import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int sz;
    private WeightedQuickUnionUF uf;
    private boolean[] isopen;
    private boolean[] topConnected;
    private boolean[] bottomConnected;
    private int openCnt;
    private boolean percolated;

    private void checkRowCol(int row, int col) {
        if (row < 1 || col < 1 || row > sz || col > sz) {
            throw new IllegalArgumentException();
        }
    }

    private int rowColToIdx(int row, int col) {
        return (row - 1) * sz + col - 1;
    }

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        sz = n;
        uf = new WeightedQuickUnionUF(n * n);
        isopen = new boolean[n * n];
        topConnected = new boolean[n * n];
        bottomConnected = new boolean[n * n];
        for (int col = 1; col <= n; ++col) {
            topConnected[rowColToIdx(1, col)] = true;
            bottomConnected[rowColToIdx(n, col)] = true;
        }
        openCnt = 0;
        percolated = false;
    }

    private void connect(int idx, int idx2) {
        if (isopen[idx2]) {
            final int idxParent = uf.find(idx);
            final int idxParent2 = uf.find(idx2);
            final boolean topConn = topConnected[idxParent] || topConnected[idxParent2];
            final boolean bottomConn = bottomConnected[idxParent] || bottomConnected[idxParent2];
            uf.union(idx, idx2);
            final int newParent = uf.find(idx);
            topConnected[newParent] = topConn;
            bottomConnected[newParent] = bottomConn;
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkRowCol(row, col);
        int idx = rowColToIdx(row, col);
        if (isopen[idx]) {
            return;
        }
        isopen[idx] = true;
        ++openCnt;
        if (row > 1) {
            connect(idx, rowColToIdx(row - 1, col));
        }
        if (row < sz) {
            connect(idx, rowColToIdx(row + 1, col));
        }
        if (col > 1) {
            connect(idx, rowColToIdx(row, col - 1));
        }
        if (col < sz) {
            connect(idx, rowColToIdx(row, col + 1));
        }
        final int parentIdx = uf.find(idx);
        if (topConnected[parentIdx] && bottomConnected[parentIdx]) {
            percolated = true;
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        return isopen[rowColToIdx(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        int idx = rowColToIdx(row, col);
        return isopen[idx] && topConnected[uf.find(idx)];
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCnt;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolated;
    }

    // test client (optional)
    public static void main(String[] args) {
        // Percolation perc = new Percolation(1);
        // StdOut.println(perc.percolates());
        // perc.open(1, 1);
        // StdOut.println(perc.percolates());

        int sz = 3;
        Percolation perc = new Percolation(sz);
        perc.open(1, 1);
        perc.open(2, 1);
        StdOut.println(perc.percolates());
        perc.open(3, 1);
        StdOut.println(perc.percolates());
        perc.open(3, 3);
        StdOut.println(perc.isOpen(3, 1));
        StdOut.println(perc.isOpen(3, 3));
        StdOut.println(perc.isFull(3, 1));
        StdOut.println(perc.isFull(3, 3));
    }
}
