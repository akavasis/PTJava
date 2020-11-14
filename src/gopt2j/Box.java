package gopt2j;

class Box {

    public Vector Min;
    public Vector Max;
    boolean left;
    boolean right;

    Box() {
    }

    Box(Vector min, Vector max) {
        Min = min;
        Max = max;
    }

    static Box BoxForShapes(IShape[] shapes) {
        if (shapes.length == 0) {
            return new Box();
        }
        Box box = shapes[0].BoundingBox();

        for (IShape shape : shapes) {
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
        return Min.x <= b.x && Max.x >= b.x && Min.y <= b.y && Max.y >= b.y && Min.z <= b.z && Max.z >= b.z;
    }

    public boolean Intersects(Box b) {
        return !(Min.x > b.Max.x || Max.x < b.Min.x || Min.y > b.Max.y || Max.y < b.Min.y || Min.z > b.Max.z || Max.z < b.Min.z);
    }

    public Double[] Intersect(Ray r) {
        var x1 = (Min.x - r.Origin.x) / r.Direction.x;
        var y1 = (Min.y - r.Origin.y) / r.Direction.y;
        var z1 = (Min.z - r.Origin.z) / r.Direction.z;
        var x2 = (Max.x - r.Origin.x) / r.Direction.x;
        var y2 = (Max.y - r.Origin.y) / r.Direction.y;
        var z2 = (Max.z - r.Origin.z) / r.Direction.z;
        
        if(x1 > x2)
        {
            Tuple2<Double,Double> x1_x2 = Tuple.valueOf(x2, x1);
            x1 = x1_x2._0;
            x2 = x1_x2._1;
        }
        if(y1 > y2)
        {
            Tuple2<Double,Double> y1_y2 = Tuple.valueOf(y2, y1);
            y1 = y1_y2._0;
            y2 = y1_y2._1;
        }
        if(z1 > z2)
        {
            Tuple2<Double,Double> z1_z2 = Tuple.valueOf(z2, z1);
            z1 = z1_z2._0;
            z2 = z1_z2._1;
        }
        
        double t1 = Math.max(Math.max(x1, y1), z1);
        double t2 = Math.min(Math.min(x2, y2), z2);
        Double intersect[] = {t1, t2};
        return intersect;
    }

    public boolean[] Partition(Axis axis, double point) {
        switch (axis) {
            case AxisX:
                left = Min.x <= point;
                right = Max.x >= point;
                break;
            case AxisY:
                left = Min.y <= point;
                right = Max.y >= point;
                break;
            case AxisZ:
                left = Min.z <= point;
                right = Max.z >= point;
                break;
        }
        boolean partition[] = {left, right};
        return partition;
    }
}
