import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {
    private SET<Point2D> points;

    public PointSET() {                              // construct an empty set of points
        points = new SET<>();
    }
    
    public boolean isEmpty() {                     // is the set empty?
        return points.isEmpty();
    }
    
    public int size() {                        // number of points in the set
        return points.size();
    }
    
    public void insert(Point2D p) {             // add the point to the set (if it is not already in the set)
        if (p == null) {
            throw new IllegalArgumentException();
        }
        points.add(p);
    }
    
    public boolean contains(Point2D p) {           // does the set contain point p?
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return points.contains(p);
    }
    
    public void draw() {                         // draw all points to standard draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : points) {
            StdDraw.point(p.x(), p.y());
        }
    }
    
    public Iterable<Point2D> range(RectHV rect) {             // all points that are inside the rectangle (or on the boundary)
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Queue<Point2D> res = new Queue<>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                res.enqueue(point);
            }
        }
        return res;
    }
    
    public Point2D nearest(Point2D p) {             // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new IllegalArgumentException();
        }
        double minDist = Double.POSITIVE_INFINITY;
        Point2D minPoint = null;
        for (Point2D point : points) {
            double dist = point.distanceSquaredTo(p);
            if (dist < minDist) {
                minDist = dist;
                minPoint = point;
            }
        }
        return minPoint;
    }
    
    public static void main(String[] args) {                  // unit testing of the methods (optional)
        PointSET pset = new PointSET();
        StdOut.println("isEmpty = " + pset.isEmpty());
        StdOut.println("size = " + pset.size());
        pset.insert(new Point2D(0.1, 0.1));
        pset.insert(new Point2D(0.2, 0.2));
        pset.insert(new Point2D(0.3, 0.3));
        pset.insert(new Point2D(0.4, 0.4));
        StdOut.println("isEmpty = " + pset.isEmpty());
        StdOut.println("size = " + pset.size());

        RectHV rect = new RectHV(0, 0, 1, 1);
        StdOut.println("points in " + rect + ":");
        for (Point2D point : pset.range(rect)) {
            StdOut.println(point);
        }

        Point2D testPoint = new Point2D(0.5, 0.5);
        StdOut.println("nearest point to " + testPoint + " is " + pset.nearest(testPoint));

        pset.draw();
    }
}
