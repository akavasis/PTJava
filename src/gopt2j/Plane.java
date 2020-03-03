package gopt2j;

class Plane extends TransformedShape implements IShape {

    Vector Point;
    Vector Normal;
    Material Material;
    Box box;

    Plane() {
    }

    Plane(Vector point, Vector normal, Material mat) {
        Point = point;
        Normal = normal;
        Material = mat;
        box = new Box(new Vector(-Util.INF, -Util.INF, -Util.INF), new Vector(Util.INF, Util.INF, Util.INF));
    }

    public static Plane NewPlane(Vector point, Vector normal, Material material) {
        return new Plane(point, normal.Normalize(), material);
    }

    @Override
    public Box BoundingBox() {
        return new Box(new Vector(-Util.INF, -Util.INF, -Util.INF), new Vector(Util.INF, Util.INF, Util.INF));
    }

    @Override
    public Hit Intersect(Ray ray) {
        double d = this.Normal.Dot(ray.Direction);

        if (Math.abs(d) < Util.EPS) {
            return Hit.NoHit;
        }

        Vector a = this.Point.Sub(ray.Origin);
        double t = a.Dot(this.Normal) / d;

        if (t < Util.EPS) {
            return Hit.NoHit;
        }

        return new Hit(this, t, null);
    }

    @Override
    public Vector NormalAt(Vector a) {
        return this.Normal;
    }

    @Override
    public Vector UV(Vector a) {
        return new Vector();
    }

    @Override
    public Material MaterialAt(Vector a) {
        return this.Material;
    }

    @Override
    public void Compile() {

    }
}
