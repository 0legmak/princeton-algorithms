import java.util.Arrays;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int[][] tiles;
    private final int cachedHamming;
    private final int cachedManhattan;
    private final int blankRow;
    private final int blankCol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        final int n = tiles.length;
        this.tiles = new int[n][n];
        int hammingDist = 0;
        int manhattanDist = 0;
        int blankR = 0;
        int blankC = 0;
        for (int r = 0; r < n; ++r) {
            for (int c = 0; c < n; ++c) {
                final int val = tiles[r][c];
                this.tiles[r][c] = val;
                if (val != 0) {
                    final int expectedRow = (val - 1) / n;
                    final int expectedCol = (val - 1) % n;
                    hammingDist += (expectedRow == r && expectedCol == c ? 0 : 1);
                    manhattanDist += Math.abs(expectedRow - r) + Math.abs(expectedCol - c);
                } else {
                    blankR = r;
                    blankC = c;
                }
            }
        }
        cachedHamming = hammingDist;
        cachedManhattan = manhattanDist;
        blankRow = blankR;
        blankCol = blankC;
    }
                                           
    // string representation of this board
    public String toString() {
        final char[] SPC = {' ', '\n'};
        StringBuilder res = new StringBuilder();
        final int n = tiles.length;
        res.append(n);
        res.append('\n');
        for (int r = 0; r < n; ++r) {
            for (int c = 0; c < n; ++c) {
                res.append(tiles[r][c]);
                res.append(SPC[c / (n - 1)]);
            }
        }
        return res.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        return cachedHamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return cachedManhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (this.getClass() != y.getClass()) {
            return false;
        }
        return Arrays.deepEquals(tiles, ((Board) y).tiles);
    }

    private int[][] copyTiles() {
        final int n = tiles.length;
        int[][] copy = new int[n][];
        for (int r = 0; r < n; ++r) {
            copy[r] = tiles[r].clone();
        }
        return copy;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> q = new Queue<>();
        final int n = tiles.length;
        if (blankCol > 0) {
            int[][] tilesCopy = copyTiles();
            tilesCopy[blankRow][blankCol] = tilesCopy[blankRow][blankCol - 1];
            tilesCopy[blankRow][blankCol - 1] = 0;
            q.enqueue(new Board(tilesCopy));
        }
        if (blankCol < n - 1) {
            int[][] tilesCopy = copyTiles();
            tilesCopy[blankRow][blankCol] = tilesCopy[blankRow][blankCol + 1];
            tilesCopy[blankRow][blankCol + 1] = 0;
            q.enqueue(new Board(tilesCopy));
        }
        if (blankRow > 0) {
            int[][] tilesCopy = copyTiles();
            tilesCopy[blankRow][blankCol] = tilesCopy[blankRow - 1][blankCol];
            tilesCopy[blankRow - 1][blankCol] = 0;
            q.enqueue(new Board(tilesCopy));
        }
        if (blankRow < n - 1) {
            int[][] tilesCopy = copyTiles();
            tilesCopy[blankRow][blankCol] = tilesCopy[blankRow + 1][blankCol];
            tilesCopy[blankRow + 1][blankCol] = 0;
            q.enqueue(new Board(tilesCopy));
        }
        return q;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] tilesCopy = copyTiles();
        int r1 = 0;
        int c1 = 0;
        int r2 = 1;
        int c2 = 0;
        if (tiles[r1][c1] == 0) {
            c1 = 1;
        } else if (tiles[r2][c2] == 0) {
            c2 = 1;
        }
        int tmp = tilesCopy[r1][c1];
        tilesCopy[r1][c1] = tilesCopy[r2][c2];
        tilesCopy[r2][c2] = tmp;
        return new Board(tilesCopy);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] tiles = {
            {8, 1, 3},
            {4, 0, 2},
            {7, 6, 5},
        };
        Board board = new Board(tiles);
        StdOut.println(board);
        StdOut.println("hamming() = " + board.hamming());
        StdOut.println("manhattan() = " + board.manhattan());
        StdOut.println("neighbours:\n");
        for (Board nei : board.neighbors()) {
            StdOut.println(nei);
        }
        StdOut.println("twin:\n");
        StdOut.println(board.twin());
    }    
}
