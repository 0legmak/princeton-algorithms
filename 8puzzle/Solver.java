import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private final Stack<Board> cachedSolution;

    private class Node implements Comparable<Node> {
        
        public Board board;
        public int moves;
        public Node prev;
        public int heuristic;

        private int getHeuristic() {
            return board.manhattan();
        }

        public Node(Board board) {
            this.board = board;
            this.heuristic = getHeuristic();
        }

        public Node(Board board, Node prev) {
            this.board = board;
            this.moves = prev.moves + 1;
            this.prev = prev;
            this.heuristic = getHeuristic();
        }

        public int compareTo(Node other) {
            final int score1 = heuristic + moves;
            final int score2 = other.heuristic + other.moves;
            return score1 - score2;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> twinPq = new MinPQ<>();
        pq.insert(new Node(initial));
        twinPq.insert(new Node(initial.twin()));
        Node goal = null;
        while (true) {
            Node node = pq.delMin();
            if (node.board.isGoal()) {
                goal = node;
                break;
            }
            for (Board nei : node.board.neighbors()) {
                if (node.prev == null || !nei.equals(node.prev.board)) {
                    pq.insert(new Node(nei, node));
                }
            }

            node = twinPq.delMin();
            if (node.board.isGoal()) {
                break;
            }
            for (Board nei : node.board.neighbors()) {
                if (node.prev == null || !nei.equals(node.prev.board)) {
                    twinPq.insert(new Node(nei, node));
                }
            }
        }

        if (goal == null) {
            cachedSolution = null;
            return;
        }

        Stack<Board> st = new Stack<>();
        for (Node node = goal; node != null; node = node.prev) {
            st.push(node.board);
        }
        cachedSolution = st;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return cachedSolution != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return cachedSolution == null ? -1 : cachedSolution.size() - 1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return cachedSolution;
    }

    // test client (see below) 
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
