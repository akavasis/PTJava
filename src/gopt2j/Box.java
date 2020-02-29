package gopt2j;

import javafx.util.Pair;

class Box {

    public Vector Min;
    public Vector Max;
    boolean left;
    boolean right;

    Box() { }

    Box(Vector min, Vector max) {
        Min = min;
        Max = max;
    }

    static Box BoxForShapes(IShape[] shapes) {
        if (shapes.length == 0)
        {
            return new Box();
        }
        Box box = shapes[0].BoundingBox();
         
        for (IShape shape : shapes)
        {
            box = box.Extend(shape.BoundingBox());
        }
        return box;
    }

    static Box BoxForTriangles(Triangle[] shapes) {
        if (shapes.length == 0) {
            return new Box();
        }
        Box box = shapes[0].BoundingBox();
        for (Triangle shape : shapes) {
            box = box.Extend(shape.BoundingBox());
        }
        return box;
    }

    public Vector Anchor(Vector anchor) {
        return Min.Add(Size().Mul(anchor));
    }

    public Vector Center() {
        return Anchor(new Vector(0.5, 0.5, 0.5));
    }

    public double OuterRadius() {
        return Min.Sub(Center()).Length();
    }

    public double InnerRadius() {
        return Center().Sub(Min).MaxComponent();
    }

    public Vector Size() {
        return Max.Sub(Min);
    }

    public Box Extend(Box b) {
        return new Box(Min.Min(b.Min), Max.Max(b.Max));
    }

    public boolean Contains(Vector b) {
        return Min.X <= b.X && Max.X >= b.X && Min.Y <= b.Y && Max.Y >= b.Y && Min.Z <= b.Z && Max.Z >= b.Z;
    }

    public boolean Intersects(Box b) {
        return !(Min.X > b.Max.X || Max.X < b.Min.X || Min.Y > b.Max.Y || Max.Y < b.Min.Y || Min.Z > b.Max.Z || Max.Z < b.Min.Z);
    }

    public Pair<Double, Double> Intersect(Ray r) {

        double x1 = (Min.X - r.Origin.X) / r.Direction.X;
        double y1 = (Min.Y - r.Origin.Y) / r.Direction.Y;
        double z1 = (Min.Z - r.Origin.Z) / r.Direction.Z;
        double x2 = (Max.X - r.Origin.X) / r.Direction.X;
        double y2 = (Max.Y - r.Origin.Y) / r.Direction.Y;
        double z2 = (Max.Z - r.Origin.Z) / r.Direction.Z;

        if (x1 > x2) {
            //(x1, x2) = (x2, x1);
            x1 = x1 - x2;
            x2 = x1 + x2;
            x1 = x2 - x1;
        }
        if (y1 > y2) {
            //(y1, y2) = (y2, y1);
            y1 = y1 - y2;
            y2 = y1 + y2;
            y1 = y2 - y1;
        }
        if (z1 > z2) {
            //(z1, z2) = (z2, z1);
            z1 = z1 - z2;
            z2 = z1 + z2;
            z1 = z2 - z1;
        }
        double t1 = Math.max(Math.max(x1, y1), z1);
        double t2 = Math.min(Math.min(x2, y2), z2);
        return new Pair<>(t1, t2);
    }

    public Pair<Boolean, Boolean> Partition(Axis axis, double point) {
        switch (axis) {
            case AxisX:
                left = Min.X <= point;
                right = Max.X >= point;
                break;
            case AxisY:
                left = Min.Y <= point;
                right = Max.Y >= point;
                break;
            case AxisZ:
                left = Min.Z <= point;
                right = Max.Z >= point;
                break;
        }
        return new Pair<>(left, right);
    }
}
