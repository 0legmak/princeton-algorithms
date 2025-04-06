import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
        final String input = BinaryStdIn.readString();
        CircularSuffixArray suffix_array = new CircularSuffixArray(input);
        final int len = input.length();
        for (int i = 0; i < len; ++i) {
            if (suffix_array.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < len; ++i) {
            int idx = suffix_array.index(i);
            idx = idx == 0 ? len - 1 : idx - 1;
            BinaryStdOut.write(input.charAt(idx));
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        final int R = 256;
        final int first = BinaryStdIn.readInt();
        final String input = BinaryStdIn.readString();
        final int len = input.length();
        int[] char_cnt = new int[R + 1];
        for (int i = 0; i < len; ++i) {
            ++char_cnt[input.charAt(i) + 1];
        }
        for (int i = 1; i < R; ++i) {
            char_cnt[i] += char_cnt[i - 1];
        }
        int[] next = new int[len];
        char[] sorted = new char[len];
        int[] char_idx = new int[R];
        for (int i = 0; i < len; ++i) {
            final char ch = input.charAt(i);
            final int idx = char_cnt[ch] + char_idx[ch];
            next[idx] = i;
            sorted[idx] = ch;
            ++char_idx[ch];
        }
        int idx = first;
        for (int i = 0; i < len; ++i) {
            BinaryStdOut.write(sorted[idx]);
            idx = next[idx];
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        switch (args[0].charAt(0)) {
            case '-':
                transform();
                break;
            case '+':
                inverseTransform();
                break;
        }
    }

}
