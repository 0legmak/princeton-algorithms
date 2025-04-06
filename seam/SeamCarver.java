import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private static final double BORDER_ENERGY = 1000.0;

    private boolean transposed;
    private int[][] pixels;
    private double[][] energ;

    private int getR(int color) {
        return (color >> 16) & 0xFF;
    }

    private int getG(int color) {
        return (color >> 8) & 0xFF;
    }

    private int getB(int color) {
        return color & 0xFF;
    }

    private double sqr(double val) {
        return val * val;
    }

    private double calcGradientSquare(int color1, int color2) {
        return sqr(getR(color2) - getR(color1)) + sqr(getG(color2) - getG(color1)) + sqr(getB(color2) - getB(color1));
    }

    private double calcXGradientSquare(int row, int col) {
        return calcGradientSquare(pixels[row][col - 1], pixels[row][col + 1]);
    }

    private double calcYGradientSquare(int row, int col) {
        return calcGradientSquare(pixels[row - 1][col], pixels[row + 1][col]);
    }

    private double calcEnergy(int row, int col) {
        return Math.sqrt(calcXGradientSquare(row, col) + calcYGradientSquare(row, col));
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        transposed = false;
        pixels = new int[picture.height()][picture.width()];
        for (int row = 0; row < picture.height(); ++row) {
            for (int col = 0; col < picture.width(); ++col) {
                pixels[row][col] = picture.getRGB(col, row);
            }
        }
        energ = new double[picture.height()][picture.width()];
        for (int row = 1; row < picture.height() - 1; ++row) {
            for (int col = 1; col < picture.width() - 1; ++col) {
                energ[row][col] = calcEnergy(row, col);
            }
        }
        for (int row = 0; row < picture.height(); ++row) {
            energ[row][0] = energ[row][picture.width() - 1] = BORDER_ENERGY;
        }
        for (int col = 0; col < picture.width(); ++col) {
            energ[0][col] = energ[picture.height() - 1][col] = BORDER_ENERGY;
        }
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width(), height());
        for (int row = 0; row < height(); ++row) {
            for (int col = 0; col < width(); ++col) {
                pic.setRGB(col, row, transposed ? pixels[col][row] : pixels[row][col]);
            }
        }
        return pic;
    }
 
    // width of current picture
    public int width() {
        return transposed ? pixels.length : pixels[0].length;
    }
 
    // height of current picture
    public int height() {
        return transposed ? pixels[0].length : pixels.length;
    }
 
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }
        return transposed ? energ[x][y] : energ[y][x];
    }

    private void transpose() {
        int w = pixels[0].length;
        int h = pixels.length;
        int[][] trans_pixels = new int[w][h];
        double[][] trans_energ = new double[w][h];
        for (int row = 0; row < h; ++row) {
            for (int col = 0; col < w; ++col) {
                trans_pixels[col][row] = pixels[row][col];
                trans_energ[col][row] = energ[row][col];
            }
        }
        pixels = trans_pixels;
        energ = trans_energ;
        transposed = !transposed;
    }

    private int[] findSeam() {
        int w = pixels[0].length;
        int h = pixels.length;
        int[] res = new int[h];
        if (w < 3 || h < 3) {
            return res;
        }
        double[][] dist = new double[h][w];
        byte[][] prev = new byte[h][w];
        for (int row = 1; row < h - 1; ++row) {
            dist[row][0] = Double.POSITIVE_INFINITY;
            for (int col = 1; col < w - 1; ++col) {
                dist[row][col] = dist[row - 1][col];
                if (dist[row][col] > dist[row - 1][col - 1]) {
                    dist[row][col] = dist[row - 1][col - 1];
                    prev[row][col] = -1;
                }
                if (dist[row][col] > dist[row - 1][col + 1]) {
                    dist[row][col] = dist[row - 1][col + 1];
                    prev[row][col] = 1;
                }
                dist[row][col] += energ[row][col];
            }
            dist[row][w - 1] = Double.POSITIVE_INFINITY;
        }
        int last_row = h - 2;
        int min_col = 1;
        double min_dist = dist[last_row][min_col];
        for (int col = 1; col < w - 1; ++col) {
            if (min_dist > dist[last_row][col]) {
                min_dist = dist[last_row][col];
                min_col = col;
            }
        }
        int col = min_col;
        for (int row = last_row; row > 0; --row) {
            res[row] = col;
            col = col + prev[row][col];
        }
        res[0] = res[1];
        res[h - 1] = res[last_row];
        return res;
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed) {
            transpose();
        }
        return findSeam();
    }
 
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed) {
            transpose();
        }
        return findSeam();
    }

    private void removeSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        int w = pixels[0].length;
        int h = pixels.length;
        if (seam.length != h) {
            throw new IllegalArgumentException();
        }
        if (w <= 1) {
            throw new IllegalArgumentException();
        }
        int[][] new_pixels = new int[h][w - 1];
        double[][] new_energ = new double[h][w - 1];
        int prev_col = seam[0];
        for (int row = 0; row < h; ++row) {
            int col = seam[row];
            if (col < 0 || col >= w) {
                throw new IllegalArgumentException();
            }
            if (Math.abs(col - prev_col) > 1) {
                throw new IllegalArgumentException();
            }
            prev_col = col;
            System.arraycopy(pixels[row], 0, new_pixels[row], 0, col);
            System.arraycopy(pixels[row], col + 1, new_pixels[row], col, w - 1 - col);
            System.arraycopy(energ[row], 0, new_energ[row], 0, col);
            System.arraycopy(energ[row], col + 1, new_energ[row], col, w - 1 - col);
        }
        pixels = new_pixels;
        energ = new_energ;
        --w;
        for (int row = 0; row < h; ++row) {
            int[] cols = { seam[row] - 1, seam[row] };
            for (int col : cols) {
                if (col >= 0 && col < w) {
                    if (row == 0 || row == h - 1 || col == 0 || col == w - 1) {
                        energ[row][col] = BORDER_ENERGY;
                    } else {
                        energ[row][col] = calcEnergy(row, col);
                    }
                }
            }
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!transposed) {
            transpose();
        }
        removeSeam(seam);
    }
 
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (transposed) {
            transpose();
        }
        removeSeam(seam);
    }
 
    //  unit testing (optional)
    public static void main(String[] args) {

    }
}
