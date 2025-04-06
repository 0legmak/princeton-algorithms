import java.util.ArrayList;

public class CircularSuffixArray {

    private class CircularSuffix implements Comparable<CircularSuffix> {

        private final int index;

        public CircularSuffix(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public int compareTo(CircularSuffix other) {
            final int len = str.length();
            for (int i = 0; i < len; ++i) {
                final int ch1 = str.charAt((i + index) % len);
                final int ch2 = str.charAt((i + other.index) % len);
                final int cmp = ch1 - ch2;
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }

    }

    private final String str;
    private final ArrayList<CircularSuffix> suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        str = s;
        suffixes = new ArrayList<>();
        for (int i = 0; i < str.length(); ++i) {
            suffixes.add(new CircularSuffix(i));
        }
        suffixes.sort(null);
    }

    // length of s
    public int length() {
        return str.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= str.length()) {
            throw new IllegalArgumentException();
        }
        return suffixes.get(i).getIndex();
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suf = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < suf.length(); ++i) {
            System.out.println(suf.index(i));
        }
    }

}
