import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    
    private class TreeNode {
        public Point2D point;
        public TreeNode left;
        public TreeNode right;
        public TreeNode(Point2D point) {
            this.point = point;
        }
    }

    private TreeNode root;
    private int count;

    public KdTree() {                              // construct an empty set of points
    }
    
    public boolean isEmpty() {                     // is the set empty?
        return count == 0;
    }
    
    public int size() {                        // number of points in the set
        return count;
    }
    
    private TreeNode insert(TreeNode node, Point2D point, boolean useX) {
        if (node == null) {
            count++;
            return new TreeNode(point);
        }
        if (node.point.x() == point.x() && node.point.y() == point.y()) {
            return node;
        }
        boolean goLeft = useX ? (point.x() < node.point.x()) : (point.y() < node.point.y());
        if (goLeft) {
            node.left = insert(node.left, point, !useX);
        } else {
            node.right = insert(node.right, point, !useX);
        }
        return node;
    }

    public void insert(Point2D p) {             // add the point to the set (if it is not already in the set)
        if (p == null) {
            throw new IllegalArgumentException();
        }
        root = insert(root, p, true);
    }
    
    private TreeNode find(TreeNode node, Point2D point, boolean useX) {
        if (node == null) {
            return null;
        }
        if (node.point.equals(point)) {
            return node;
        }
        boolean goLeft = useX ? (point.x() < node.point.x()) : (point.y() < node.point.y());
        return find(goLeft ? node.left : node.right, point, !useX);
    }

    public boolean contains(Point2D p) {           // does the set contain point p?
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return find(root, p, true) != null;
    }
    
    private void internalDraw(TreeNode node, boolean useX, RectHV rect) {
        if (node == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.point.x(), node.point.y());
        StdDraw.setPenRadius();
        if (useX) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), rect.ymin(), node.point.x(), rect.ymax());
            internalDraw(node.left, !useX, new RectHV(rect.xmin(), rect.ymin(), node.point.x(), rect.ymax()));
            internalDraw(node.right, !useX, new RectHV(node.point.x(), rect.ymin(), rect.xmax(), rect.ymax()));
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), node.point.y(), rect.xmax(), node.point.y());
            internalDraw(node.left, !useX, new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.point.y()));
            internalDraw(node.right, !useX, new RectHV(rect.xmin(), node.point.y(), rect.xmax(), rect.ymax()));
        }
    }

    public void draw() {                         // draw all points to standard draw
        internalDraw(root, true, new RectHV(0, 0, 1, 1));
    }
    
    private void rangeQuery(Queue<Point2D> result, TreeNode node, RectHV nodeRect, RectHV rect, boolean useX) {
        if (node == null) {
            return;
        }
        if (rect.contains(node.point)) {
            result.enqueue(node.point);
        }
        RectHV leftNodeRect, rightNodeRect;
        if (useX) {
            leftNodeRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.point.x(), nodeRect.ymax());
            rightNodeRect = new RectHV(node.point.x(), nodeRect.ymin(), nodeRect.xmax(), nodeRect.ymax());
        } else {
            leftNodeRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(), node.point.y());
            rightNodeRect = new RectHV(nodeRect.xmin(), node.point.y(), nodeRect.xmax(), nodeRect.ymax());
        }
        if (rect.intersects(leftNodeRect)) {
            rangeQuery(result, node.left, leftNodeRect, rect, !useX);
        }
        if (rect.intersects(rightNodeRect)) {
            rangeQuery(result, node.right, rightNodeRect, rect, !useX);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {             // all points that are inside the rectangle (or on the boundary)
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Queue<Point2D> result = new Queue<>();
        rangeQuery(result, root, new RectHV(0, 0, 1, 1), rect, true);
        return result;
    }

    private class NearestQuery {

        private Point2D origin;
        private double shortestDist;
        private Point2D candidate;

        NearestQuery(TreeNode node, Point2D origin) {
            this.origin = origin;
            shortestDist = Double.POSITIVE_INFINITY;
            exec(node, new RectHV(0, 0, 1, 1), true);
        }

        public Point2D result() {
            return candidate;
        }

        private void exec(TreeNode node, RectHV nodeRect, boolean useX) {
            if (node == null) {
                return;
            }
            double dist = node.point.distanceSquaredTo(origin);
            if (dist < shortestDist) {
                shortestDist = dist;
                candidate = node.point;
            }

            RectHV leftNodeRect, rightNodeRect;
            boolean goLeft;
            if (useX) {
                leftNodeRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.point.x(), nodeRect.ymax());
                rightNodeRect = new RectHV(node.point.x(), nodeRect.ymin(), nodeRect.xmax(), nodeRect.ymax());
                goLeft = origin.x() < node.point.x();
            } else {
                leftNodeRect = new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(), node.point.y());
                rightNodeRect = new RectHV(nodeRect.xmin(), node.point.y(), nodeRect.xmax(), nodeRect.ymax());
                goLeft = origin.y() < node.point.y();
            }
            if (goLeft) {
                if (leftNodeRect.distanceSquaredTo(origin) < shortestDist) {
                    exec(node.left, leftNodeRect, !useX);
                }
                if (rightNodeRect.distanceSquaredTo(origin) < shortestDist) {
                    exec(node.right, rightNodeRect, !useX);
                }
            } else {
                if (rightNodeRect.distanceSquaredTo(origin) < shortestDist) {
                    exec(node.right, rightNodeRect, !useX);
                }
                if (leftNodeRect.distanceSquaredTo(origin) < shortestDist) {
                    exec(node.left, leftNodeRect, !useX);
                }
            }
        }
    }

    public Point2D nearest(Point2D p) {             // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new IllegalArgumentException();
        }
        NearestQuery query = new NearestQuery(root, p);
        return query.result();
    }
    
    public static void main(String[] args) {                  // unit testing of the methods (optional)
        KdTree tree = new KdTree();
        // tree.insert(new Point2D(0.1, 0.1));
        // tree.insert(new Point2D(0.2, 0.2));
        // tree.insert(new Point2D(0.3, 0.3));
        // tree.insert(new Point2D(0.4, 0.4));
        // tree.draw();


        // tree.insert(new Point2D(0.0, 1.0));
        // tree.insert(new Point2D(0.0, 0.0));
        // tree.insert(new Point2D(0.0, 1.0));
        // tree.insert(new Point2D(0.0, 0.0));
        // for (Point2D point : tree.range(new RectHV(0.0, 0.0, 0.0, 1.0))) {
        //     StdOut.println(point);
        // }

        tree.insert(new Point2D(0.372, 0.497));
        tree.insert(new Point2D(0.564, 0.413));
        tree.insert(new Point2D(0.226, 0.577));
        tree.insert(new Point2D(0.144, 0.179));
        tree.insert(new Point2D(0.083, 0.51));
        tree.insert(new Point2D(0.32, 0.708));
        tree.insert(new Point2D(0.417, 0.362));
        tree.insert(new Point2D(0.862, 0.825));
        tree.insert(new Point2D(0.785, 0.725));
        tree.insert(new Point2D(0.499, 0.208));
        StdOut.println(tree.nearest(new Point2D(0.55, 0.83)));
        tree.draw();
    }
}
