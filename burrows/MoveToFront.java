import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] idx_to_char = new char[R];
        int[] char_to_idx = new int[R];
        for (int i = 0; i < R; ++i) {
            idx_to_char[i] = (char) i;
            char_to_idx[i] = i;
        }
        while (!BinaryStdIn.isEmpty()) {
            final char ch = BinaryStdIn.readChar();
            final int idx = char_to_idx[ch];
            BinaryStdOut.write((char) idx);
            for (int i = idx; i > 0; --i) {
                final char ch2 = idx_to_char[i - 1];
                idx_to_char[i] = ch2;
                char_to_idx[ch2] = i;
            }
            idx_to_char[0] = ch;
            char_to_idx[ch] = 0;
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] idx_to_char = new char[R];
        for (int i = 0; i < R; ++i) {
            idx_to_char[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            final int idx = BinaryStdIn.readChar();
            final char ch = idx_to_char[idx];
            BinaryStdOut.write(ch);
            for (int i = idx; i > 0; --i) {
                final char ch2 = idx_to_char[i - 1];
                idx_to_char[i] = ch2;
            }
            idx_to_char[0] = ch;
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        switch (args[0].charAt(0)) {
            case '-':
                encode();
                break;
            case '+':
                decode();
                break;
        }
    }

}
