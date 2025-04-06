public class CircularSuffixArray {

    private static final int INSERTION_CUTOFF = 15;
    private final char[] str;
    private final int str_len;
    private final int[] suffixes;

    private void swap(int a, int b) {
        final int tmp = suffixes[a];
        suffixes[a] = suffixes[b];
        suffixes[b] = tmp;
    }

    private boolean less(int suffix1, int suffix2, int char_idx) {
        int idx1 = suffixes[suffix1] + char_idx;
        int idx2 = suffixes[suffix2] + char_idx;
        for (int i = char_idx; i < str_len; ++i, ++idx1, ++idx2) {
            final int cmp = (int)str[idx1] - str[idx2];
            if (cmp != 0) {
                return cmp < 0;
            }
        }
        return false;
    }

    private void sort(int begin_idx, int end_idx, int char_idx) {
        if (end_idx <= begin_idx || char_idx == str_len) {
            return;
        }
        if (end_idx - begin_idx + 1 <= INSERTION_CUTOFF) {
            // insertion sort
            for (int i = begin_idx + 1; i <= end_idx; ++i) {
                for (int j = i; j > begin_idx && less(j, j - 1, char_idx); --j) {
                    swap(j, j - 1);
                }
            }
            return;
        }
        // 3-way partition
        int left_idx = begin_idx;
        int right_idx = end_idx;
        final int pval = str[(suffixes[left_idx] + char_idx)];
        for (int idx = left_idx; idx <= right_idx;) {
            final int cmp = str[(suffixes[idx] + char_idx)] - pval;
            if (cmp < 0) {
                swap(left_idx, idx);
                ++left_idx;
                ++idx;
            } else if (cmp > 0) {
                swap(right_idx, idx);
                --right_idx;
            } else {
                ++idx;
            }
        }
        // recur
        sort(begin_idx, left_idx - 1, char_idx);
        sort(left_idx, right_idx, char_idx + 1);
        sort(right_idx + 1, end_idx, char_idx);
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        str_len = s.length();
        str = new char[2 * str_len];
        for (int i = 0; i < 2 * str_len; ++i) {
            str[i] = s.charAt(i % str_len);
        }
        suffixes = new int[str_len];
        for (int i = 0; i < str_len; ++i) {
            suffixes[i] = i;
        }
        sort(0, str_len - 1, 0);
    }

    // length of s
    public int length() {
        return str_len;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= str_len) {
            throw new IllegalArgumentException();
        }
        return suffixes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suf = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < suf.length(); ++i) {
            System.out.println(suf.index(i));
        }
    }

}
