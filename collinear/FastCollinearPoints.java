import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Merge;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private LineSegment[] lineSegments;
    private Segment[] segments;
    private int segmentCnt;

    private void addSegment(Point pt1, Point pt2) {
        if (segmentCnt == segments.length) {
            Segment[] newSeg = new Segment[segments.length == 0 ? 1 : segments.length * 2];
            for (int i = 0; i < segments.length; ++i) {
                newSeg[i] = segments[i];
            }
            segments = newSeg;
        }
        segments[segmentCnt] = new Segment(pt1, pt2);
        segmentCnt++;
    }

    private void shrink() {
        if (segments.length > segmentCnt) {
            Segment[] newSeg = new Segment[segmentCnt];
            for (int i = 0; i < segmentCnt; ++i) {
                newSeg[i] = segments[i];
            }
            segments = newSeg;
        }
    }

    private static void swap(Point[] points, int i, int j) {
        Point tmp = points[i];
        points[i] = points[j];
        points[j] = tmp;
    }

    private class Segment implements Comparable<Segment> {

        public Point begin;
        public Point end;

        public Segment(Point begin, Point end) {
            this.begin = begin;
            this.end = end;
        }

        public int compareTo(Segment other) {
            int res = begin.compareTo(other.begin);
            if (res != 0) {
                return res;
            }
            return end.compareTo(other.end);
        }
    }

    public FastCollinearPoints(Point[] points) {    // finds all line segments containing 4 or more points
        if (points == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        Point[] sortedPoints = new Point[points.length];
        for (int i = 0; i < points.length; ++i) {
            sortedPoints[i] = points[i];
        }
        Merge.sort(sortedPoints);
        for (int i = 1; i < sortedPoints.length; ++i) {
            if (sortedPoints[i].equals(sortedPoints[i - 1])) {
                throw new IllegalArgumentException();
            }
        }
        segments = new Segment[0];
        Double[] slopes = new Double[points.length - 1];
        final int lastIdx = points.length - 1;
        for (int i = 0; i < points.length; ++i) {
            swap(points, i, lastIdx);
            for (int j = 0; j < lastIdx; ++j) {
                slopes[j] = points[lastIdx].slopeTo(points[j]);
            }
            int[] indices = Merge.indexSort(slopes);
            for (int j = 0; j < indices.length;) {
                int cnt = 0;
                for (int k = j; k < indices.length && slopes[indices[j]].equals(slopes[indices[k]]); ++k) {
                    cnt++;
                }
                if (cnt >= 3) {
                    Point minPt = points[lastIdx];
                    Point maxPt = minPt;
                    for (int c = 0; c < cnt; ++c) {
                        Point pt = points[indices[j + c]];
                        if (pt.compareTo(minPt) < 0) {
                            minPt = pt;
                        }
                        if (pt.compareTo(maxPt) > 0) {
                            maxPt = pt;
                        }
                    }
                    addSegment(minPt, maxPt);
                }
                j += cnt;
            }
            swap(points, i, lastIdx);
        }

        shrink();
        Merge.sort(segments);
        int readIdx = 0;
        int writeIdx = 0;
        while (writeIdx < segments.length && readIdx < segments.length) {
            segments[writeIdx] = segments[readIdx];
            readIdx++;
            while (readIdx < segments.length && segments[readIdx].compareTo(segments[writeIdx]) == 0) {
                readIdx++;
            }
            writeIdx++;
        }
        segmentCnt = writeIdx;
        shrink();

        lineSegments = new LineSegment[segmentCnt];
        for (int i = 0; i < segmentCnt; ++i) {
            lineSegments[i] = new LineSegment(segments[i].begin, segments[i].end);
        }
        segments = null;
    }

    public int numberOfSegments() {       // the number of line segments
        return segmentCnt;
    }

    public LineSegment[] segments() {               // the line segments
        LineSegment[] res = new LineSegment[segmentCnt];
        for (int i = 0; i < segmentCnt; ++i) {
            res[i] = lineSegments[i];
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
