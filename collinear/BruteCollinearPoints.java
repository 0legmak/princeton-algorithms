import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private LineSegment[] segments;
    private int segmentCnt;

    private void addSegment(Point pt1, Point pt2) {
        if (segmentCnt == segments.length) {
            LineSegment[] newSeg = new LineSegment[segments.length == 0 ? 1 : segments.length * 2];
            for (int i = 0; i < segments.length; ++i) {
                newSeg[i] = segments[i];
            }
            segments = newSeg;
        }
        segments[segmentCnt] = new LineSegment(pt1, pt2);
        segmentCnt++;
    }

    private void shrink() {
        if (segments.length > segmentCnt) {
            LineSegment[] newSeg = new LineSegment[segmentCnt];
            for (int i = 0; i < segmentCnt; ++i) {
                newSeg[i] = segments[i];
            }
            segments = newSeg;
        }
    }

    private Point getMin(Point pt1, Point pt2, Point pt3, Point pt4) {
        Point res = pt1;
        if (pt2.compareTo(res) < 0) {
            res = pt2;
        }
        if (pt3.compareTo(res) < 0) {
            res = pt3;
        }
        if (pt4.compareTo(res) < 0) {
            res = pt4;
        }
        return res;
    }

    private Point getMax(Point pt1, Point pt2, Point pt3, Point pt4) {
        Point res = pt1;
        if (pt2.compareTo(res) > 0) {
            res = pt2;
        }
        if (pt3.compareTo(res) > 0) {
            res = pt3;
        }
        if (pt4.compareTo(res) > 0) {
            res = pt4;
        }
        return res;
    }

    public BruteCollinearPoints(Point[] points) {   // finds all line segments containing 4 points
        if (points == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        segments = new LineSegment[0];
        for (int idx1 = 0; idx1 < points.length; ++idx1) {
            for (int idx2 = idx1 + 1; idx2 < points.length; ++idx2) {
                if (points[idx1].equals(points[idx2])) {
                    throw new IllegalArgumentException();
                }
                double slope2 = points[idx1].slopeTo(points[idx2]);
                for (int idx3 = idx2 + 1; idx3 < points.length; ++idx3) {
                    double slope3 = points[idx1].slopeTo(points[idx3]);
                    if (slope2 == slope3) {
                        for (int idx4 = idx3 + 1; idx4 < points.length; ++idx4) {
                            double slope4 = points[idx1].slopeTo(points[idx4]);
                            if (slope3 == slope4) {
                                // StdOut.println(points[idx1] + ", " + points[idx2] + ", " + points[idx3] + ", " + points[idx4]);
                                addSegment(
                                    getMin(points[idx1], points[idx2], points[idx3], points[idx4]),
                                    getMax(points[idx1], points[idx2], points[idx3], points[idx4])
                                );
                            }
                        }
                    }
                }
            }
        }
        shrink();
    }

    public int numberOfSegments() {       // the number of line segments
        return segmentCnt;
    }

    public LineSegment[] segments() {               // the line segments
        LineSegment[] res = new LineSegment[segmentCnt];
        for (int i = 0; i < segmentCnt; ++i) {
            res[i] = segments[i];
        }
        return res;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
