import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Stack;

/**
 * Created by justinelsemah on 2017-07-29.
 */
public class KdTree {

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean vertical;

        private Node(Point2D p) {
            this.p = p;
        }
    }

    private int size;
    private Node root;
    private double closestDistance;
    private Point2D nearest;

    // construct an empty set of points
    public KdTree() {
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {

        if (p == null) {
            throw new IllegalArgumentException();
        }


        root = insert(root, null, p, true);
    }

    private Node insert(Node node, Node parent, Point2D p, boolean vertical) {
        if (node == null) {
            size++;
            Node childNode = new Node(p);
            childNode.vertical = vertical;

            if (parent == null) {
                childNode.rect = new RectHV(0, 0, 1, 1);
            } else {
                if (vertical) {
                    if (childNode.p.y() < parent.p.y()) {
                        childNode.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.rect.xmax(), parent.p.y());
                    } else {
                        childNode.rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.rect.ymax());
                    }
                } else {
                    if (childNode.p.x() < parent.p.x()) {
                        childNode.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());
                    } else {
                        childNode.rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());
                    }
                }
            }

            return childNode;
        }

        if (node.p.equals(p)) {
            return node;
        }

        if (vertical) {
            if (p.x() < node.p.x()) {
                node.lb = insert(node.lb, node, p, false);
            } else {
                node.rt = insert(node.rt, node, p, false);
            }
        } else {
            if (p.y() < node.p.y()) {
                node.lb = insert(node.lb, node, p, true);
            } else {
                node.rt = insert(node.rt, node, p, true);
            }
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        return contains(root, p, true);
    }

    private boolean contains(Node node, Point2D p, boolean vertical) {
        if (node == null) {
            return false;
        }

        if (node.p.equals(p)) {
            return true;
        }

        if (vertical) {
            if (p.x() < node.p.x()) {
                return contains(node.lb, p, false);
            } else {
                return contains(node.rt, p, false);
            }
        } else {
            if (p.y() < node.p.y()) {
                return contains(node.lb, p, true);
            } else {
                return contains(node.rt, p, true);
            }
        }
    }

    // draw all points to standard draw
    public void draw() {
        drawHelper(root, null);
    }

    private void drawHelper(Node node, Node parent) {
        // draw left path of BST
        if (!(node.lb == null)) {
            drawHelper(node.lb, node);
        }

        // draw current point on BST
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.p.x(), node.p.y());
        if (parent == null) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(node.p.x(), 0, node.p.x(), 1);
        } else if (node.vertical) {
            if (node.equals(parent.lb)) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.setPenRadius();
                StdDraw.line(node.p.x(), parent.rect.ymin(), node.p.x(), parent.p.y());
            } else {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.setPenRadius();
                StdDraw.line(node.p.x(), parent.p.y(), node.p.x(), parent.rect.ymax());
            }
        } else {
            if (node.equals(parent.lb)) {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.setPenRadius();
                StdDraw.line(parent.rect.xmin(), node.p.y(), parent.p.x(), node.p.y());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.setPenRadius();
                StdDraw.line(parent.p.x(), node.p.y(), parent.rect.xmax(), node.p.y());
            }
        }

        // draw left path of BST
        if (!(node.rt == null)) {
            drawHelper(node.rt, node);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }

        Stack<Point2D> stack = new Stack<>();

        rangeHelper(root, stack, rect);

        return stack;
    }

    private void rangeHelper(Node node, Stack<Point2D> stack, RectHV rect) {
        if (node.rect.intersects(rect)) {
            if (rect.contains(node.p)) {
                stack.push(node.p);
            }
            if (node.lb != null) {
                rangeHelper(node.lb, stack, rect);
            }
            if (node.rt != null) {
                rangeHelper(node.rt, stack, rect);
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        if (this.isEmpty()) {
            return null;
        }

        nearest = root.p;
        closestDistance = root.p.distanceSquaredTo(p);
        nearestHelper(root, p);
        closestDistance = 0;
        Point2D temp = nearest;
        nearest = null;
        return temp;
    }

    private void nearestHelper(Node node, Point2D p) {
        if (node.rect.distanceSquaredTo(p) < closestDistance) {
            if (node.p.distanceSquaredTo(p) < closestDistance) {
                this.nearest = node.p;
                closestDistance = node.p.distanceSquaredTo(p);
            }
            if (node.lb != null) {
                nearestHelper(node.lb, p);
            }
            if (node.rt != null) {
                nearestHelper(node.rt, p);
            }
        }
    }
}
