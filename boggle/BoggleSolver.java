import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int ALPHABET_SIZE = 'Z' - 'A' + 1;
        
    private class TrieNode {
        public int word_index;
        public TrieNode[] links;
        public TrieNode() {
            word_index = -1;
            links = new TrieNode[ALPHABET_SIZE];
        }
        public boolean isWord() {
            return word_index != -1;
        }
    }

    private final ArrayList<String> words;
    private final TrieNode dict_trie;
    private final boolean[] found_word_indices;

    private TrieNode addLetter(TrieNode node, char letter) {
        final int idx = letter - 'A';
        TrieNode next_node = node.links[idx];
        if (next_node == null) {
            next_node = new TrieNode();
            node.links[idx] = next_node;
        }
        return next_node;
    }

    private boolean addWord(int index, String word) {
        if (word.length() < 3) {
            return false;
        }
        TrieNode node = dict_trie;
        int idx = 0;
        while (idx < word.length()) {
            final char letter = word.charAt(idx);
            node = addLetter(node, letter);
            if (letter == 'Q') {
                ++idx;
                if (idx == word.length() || word.charAt(idx) != 'U') {
                    return false;
                }
            }
            ++idx;
        }
        if (node.isWord()) {
            return false;
        }
        node.word_index = index;
        return true;
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        words = new ArrayList<String>(dictionary.length);
        dict_trie = new TrieNode();
        int index = 0;
        for (final String word : dictionary) {
            if (addWord(index, word)) {
                words.add(word);
                ++index;
            }
        }
        found_word_indices = new boolean[index];
    }

    private class BoardSearch {

        private class RowCol {
            public final int row;
            public final int col;
            RowCol(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }

        private final int rows;
        private final int cols;
        private final int[][] board;
        private final ArrayList<ArrayList<ArrayList<RowCol>>> board_graph;
        private final boolean[][] visited;
        private final ArrayList<String> found_words;

        public BoardSearch(BoggleBoard board) {
            rows = board.rows();
            cols = board.cols();
            this.board = new int[rows][cols];
            board_graph = new ArrayList<>(rows);
            for (int row = 0; row < rows; ++row) {
                board_graph.add(new ArrayList<>(cols));
                for (int col = 0; col < cols; ++col) {
                    board_graph.get(row).add(new ArrayList<>());
                    this.board[row][col] = board.getLetter(row, col) - 'A';
                    ArrayList<RowCol> adj_list = board_graph.get(row).get(col);
                    for (int next_row = Math.max(row - 1, 0); next_row <= Math.min(row + 1, rows - 1); ++next_row) {
                        for (int next_col = Math.max(col - 1, 0); next_col <= Math.min(col + 1, cols - 1); ++next_col) {
                            if (next_row != row || next_col != col) {
                                adj_list.add(new RowCol(next_row, next_col));
                            }
                        }
                    }
                }
            }
            this.visited = new boolean[rows][cols];
            found_words = new ArrayList<String>();
            Arrays.fill(found_word_indices, false);
            for (int row = 0; row < rows; ++row) {
                for (int col = 0; col < cols; ++col) {
                    search(row, col, dict_trie);
                }
            }
        }

        ArrayList<String> result() {
            return found_words;
        }

        private void search(int row, int col, TrieNode node) {
            TrieNode next_node = node.links[board[row][col]];
            if (next_node == null) {
                return;
            }
            if (next_node.isWord() && !found_word_indices[next_node.word_index]) {
                found_words.add(words.get(next_node.word_index));
                found_word_indices[next_node.word_index] = true;
            }
            visited[row][col] = true;
            for (final RowCol next : board_graph.get(row).get(col)) {
                if (!visited[next.row][next.col]) {
                    search(next.row, next.col, next_node);
                }
            }
            visited[row][col] = false;
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        BoardSearch search = new BoardSearch(board);
        return search.result();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word.length() < 3) {
            return 0;
        }
        TrieNode node = dict_trie;
        int idx = 0;
        while (idx < word.length()) {
            final char letter = word.charAt(idx);
            node = node.links[word.charAt(idx) - 'A'];
            if (node == null) {
                return 0;
            }
            if (letter == 'Q') {
                ++idx;
                if (idx == word.length() || word.charAt(idx) != 'U') {
                    return 0;
                }
            }
            ++idx;
        }
        if (!node.isWord()) {
            return 0;
        }
        switch (word.length()) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }    
}
