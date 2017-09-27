/**
 * Created by justinelsemah on 2017-07-29.
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Stack;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> binarySearchTree;

    // construct an empty set of points
    public PointSET(){
        binarySearchTree = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty(){
        return binarySearchTree.isEmpty();
    }

    // number of points in the set
    public int size(){
        return binarySearchTree.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p){
        if(p == null){
            throw new IllegalArgumentException();
        }

        binarySearchTree.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p){
        if(p == null){
            throw new IllegalArgumentException();
        }

        return binarySearchTree.contains(p);
    }

    // draw all points to standard draw
    public void draw(){
        for(Point2D point : binarySearchTree){
            point.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        if(rect == null){
            throw new IllegalArgumentException();
        }

        Stack<Point2D> stack = new Stack<>();

        for(Point2D point : binarySearchTree){
            if(rect.contains(point)){
                stack.push(point);
            }
        }

        return stack;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p){
        if(p == null){
            throw new IllegalArgumentException();
        }

        if(this.isEmpty()){
            return null;
        }

        Point2D nearest = null;
        double closestDistance = Double.MAX_VALUE;

        for(Point2D point : binarySearchTree){
            if(point.distanceSquaredTo(p) < closestDistance){
                nearest = point;
                closestDistance = point.distanceSquaredTo(p);
            }
        }
        return nearest;
    }
}
