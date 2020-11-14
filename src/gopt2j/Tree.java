package gopt2j;

import static gopt2j.Box.BoxForShapes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

class Tree {

    public Box Box;
    public Node Root;

    public Tree() {
    }

    public Tree(Box box, Node root) {
        this.Box = box;
        this.Root = root;
    }

    public Tree(IShape[] shapes) {
        System.out.println("Building k-d tree: " + shapes.length);
        Box box = BoxForShapes(shapes);
        Node node = new Node(Axis.AxisNone, 0, shapes, null, null);
        node.Split(0);
        Box = box;
        Root = node;
    }

    public Hit Intersect(Ray r) {
        
        Double[] tm = Box.Intersect(r);

        double tmin = tm[0];
        double tmax = tm[1];

        if (tmax < tmin || tmax <= 0) {
            return Hit.NoHit;
        }
        return this.Root.Intersect(r, tmin, tmax);
    }

    public class Node {

        Axis Axis;
        double Point;
        IShape[] Shapes;
        Node Left;
        Node Right;

        double tsplit;
        boolean leftFirst;

        Node(Axis axis, double point, IShape[] shapes, Node left, Node right) {
            this.Axis = axis;
            this.Point = point;
            this.Shapes = shapes;
            this.Left = left;
            this.Right = right;
        }

        Node NewNode(IShape[] shapes) {
            Node n = new Node(Axis.AxisNone, 0, shapes, null, null);
            return n;
        }

        public Hit Intersect(Ray r, double tmin, double tmax) {

            switch (Axis) {
                case AxisNone:
                    return IntersectShapes(r);
                case AxisX:
                    tsplit = (Point - r.Origin.x) / r.Direction.x;
                    leftFirst = (r.Origin.x < Point) || (r.Origin.x == Point && r.Direction.x <= 0);
                    break;
                case AxisY:
                    tsplit = (Point - r.Origin.y) / r.Direction.y;
                    leftFirst = (r.Origin.y < Point) || (r.Origin.y == Point && r.Direction.y <= 0);
                    break;
                case AxisZ:
                    tsplit = (Point - r.Origin.z) / r.Direction.z;
                    leftFirst = (r.Origin.z < Point) || (r.Origin.z == Point && r.Direction.z <= 0);
                    break;
                default:
                    break;
            }

            Node first, second;

            if (leftFirst) {
                first = Left;
                second = Right;
            } else {
                first = Right;
                second = Left;
            }

            if (tsplit > tmax || tsplit <= 0) {
                return first.Intersect(r, tmin, tmax);
            } else if (tsplit < tmin) {
                return second.Intersect(r, tmin, tmax);
            } else {
                Hit h1 = first.Intersect(r, tmin, tsplit);

                if (h1.T <= tsplit) {
                    return h1;
                }

                Hit h2 = second.Intersect(r, tsplit, Math.min(tmax, h1.T));

                if (h1.T <= h2.T) {
                    return h1;

                } else {
                    return h2;
                }
            }
        }

        public Hit IntersectShapes(Ray r) {
            Hit hit = Hit.NoHit;
            for (IShape shapes : this.Shapes) {
                if(shapes != null)
                {
                    Hit h = shapes.Intersect(r);
                    if (h.T < hit.T) {
                        hit = h;
                    }
                }
            }
            return hit;
        }

        public double Median(List<Double> list) {

            int middle = list.size() / 2;

            if (list.isEmpty()) {
                return 0;
            } else if (list.size() % 2 == 1) {
                return list.get(middle);
            } else {
                double a = list.get(list.size() / 2 - 1);
                double b = list.get(list.size() / 2);
                return (a + b) / 2;
            }
        }

        public int PartitionScore(Axis axis, double point) {
            int left = 0;
            int right = 0;
            for (IShape shape : this.Shapes) {
                
                if(shape !=null)
                {
                    Box box = shape.BoundingBox();
                    boolean[] lr = box.Partition(axis, point);
                    if (lr[0]) {
                        left++;
                    }
                    if (lr[1]) {
                        right++;
                    }
                }
                
            }
            if (left >= right) {
                return left;
            } else {
                return right;
            }
        }

        IShape[][] Partition(int size, Axis axis, double point) {
            ArrayList<IShape> left = new ArrayList<>();
            ArrayList<IShape> right = new ArrayList<>();
            
            for (IShape shape : Shapes) {
                if (shape != null) {
                    Box box = shape.BoundingBox();

                    boolean[] lr = box.Partition(axis, point);

                    if (lr[0]) {
                        left.add(shape);
                    }

                    if (lr[1]) {
            
                        right.add(shape);
                    }
                }
            }
            
            IShape[] lp = new IShape[left.size()];
            IShape[] rp = new IShape[right.size()];
            IShape[] leftp = left.toArray(lp);
            IShape[] rightp = right.toArray(rp);
            return new IShape[][]{leftp,rightp}; 

        }

        public void Split(int depth) {

            if (this.Shapes.length < 8) {
                return;
            }

            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            List<Double> zs = new ArrayList<>();

            for (IShape shape : this.Shapes) {
                
                if(shape!= null)
                {
                    Box box = shape.BoundingBox();
                    xs.add(box.Min.x);
                    xs.add(box.Max.x);
                    ys.add(box.Min.y);
                    ys.add(box.Max.y);
                    zs.add(box.Min.y);
                    zs.add(box.Max.y);
                }
                
            }

            Collections.sort(xs);
            Collections.sort(ys);
            Collections.sort(zs);
            //xs = xs.stream().sorted().collect(Collectors.toList());
            //ys = ys.stream().sorted().collect(Collectors.toList());
            //zs = zs.stream().sorted().collect(Collectors.toList());

            double mx = Median(xs);
            double my = Median(ys);
            double mz = Median(zs);

            int best = (int) (Shapes.length * 0.85);
            gopt2j.Axis bestAxis = Axis.AxisNone;
            double bestPoint = 0.0;

            int sx = PartitionScore(Axis.AxisX, mx);
            if (sx < best) {
                best = sx;
                bestAxis = Axis.AxisX;
                bestPoint = mx;
            }

            int sy = PartitionScore(Axis.AxisY, my);
            if (sy < best) {
                best = sy;
                bestAxis = Axis.AxisY;
                bestPoint = my;
            }

            int sz = PartitionScore(Axis.AxisZ, mz);
            if (sz < best) {
                best = sz;
                bestAxis = Axis.AxisZ;
                bestPoint = mz;
            }

            if (bestAxis == Axis.AxisNone) {
                return;
            }

            var lr = Partition(best, bestAxis, bestPoint);
            Axis = bestAxis;
            Point = bestPoint;
            Left = NewNode(lr[0]);
            Right = NewNode(lr[1]);
            Left.Split(depth + 1);
            Right.Split(depth + 1);
            Shapes = null; // only needed at leaf nodes
        }
    }
}
