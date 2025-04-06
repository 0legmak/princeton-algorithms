// import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Comparator;

public class CircularSuffixArray {

    private final Integer[] p;

    private class OrderPair implements Comparable<OrderPair> {
        
        public int left;
        public int right;
        
        OrderPair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(OrderPair other) {
            if (this.left != other.left) {
                return Integer.compare(this.left, other.left);
            } else {
                return Integer.compare(this.right, other.right);
            }
        }

        public boolean eq(OrderPair other) {
            return left == other.left && right == other.right;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        final int len = s.length();
        p = new Integer[len];  // suffix i -> sorted order idx
        if (len == 0) {
            return;
        }
        int[] c = new int[len];  // suffix i -> eq. class
        for (int i = 0; i < len; ++i) {
            p[i] = i;
        }
        Arrays.sort(p, (a, b) -> Integer.compare(s.charAt(a), s.charAt(b)));
        c[p[0]] = 0;
        for (int i = 1; i < len; ++i) {
            if (s.charAt(p[i]) == s.charAt(p[i - 1])) {
                c[p[i]] = c[p[i - 1]];
            } else {
                c[p[i]] = c[p[i - 1]] + 1;
            }
        }

        int k = 0;
        while ((1 << k) < len && c[p[len - 1]] != len - 1) {
            OrderPair[] pairs = new OrderPair[len];
            for (int i = 0; i < len; ++i) {
                pairs[i] = new OrderPair(c[i], c[(i + (1 << k)) % len]);
                p[i] = i;
            }
            Arrays.sort(p, (a, b) -> pairs[a].compareTo(pairs[b]));
            c[p[0]] = 0;
            for (int i = 1; i < len; ++i) {
                if (pairs[p[i]].eq(pairs[p[i - 1]])) {
                    c[p[i]] = c[p[i - 1]];
                } else {
                    c[p[i]] = c[p[i - 1]] + 1;
                }
            }
            ++k;
        }
    }

    // length of s
    public int length() {
        return p.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException();
        }
        return p[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        final String s = "ABRACADABRA!";
        CircularSuffixArray suf = new CircularSuffixArray(s);
        for (int i = 0; i < suf.length(); ++i) {
            final int idx = suf.index(i);
            System.out.println(s.substring(idx) + s.substring(0, idx) + " " + idx);
        }
    }

}
