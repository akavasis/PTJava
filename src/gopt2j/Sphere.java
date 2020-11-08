package gopt2j;

class Sphere extends TransformedShape implements IShape {

    Vector Center;
    double Radius;
    Material Material;
    Box Box;

    Sphere(Vector center_, double radius_, Material material_, Box box_) {
        Center = center_;
        Radius = radius_;
        Material = material_;
        Box = box_;
    }

    static Sphere NewSphere(Vector center, double radius, Material material) {
        Vector min = new Vector(center.x - radius, center.y - radius, center.z - radius);
        Vector max = new Vector(center.x + radius, center.y + radius, center.z + radius);
        Box box = new Box(min, max);
        return new Sphere(center, radius, material, box);
    }

    @Override
    public Box BoundingBox() {
        return Box;
    }

    @Override
    public Hit Intersect(Ray r) {
        Vector to = r.Origin.Sub(Center);
        double b = to.Dot(r.Direction);
        double c = to.Dot(to) - Radius * Radius;
        double d = b * b - c;
        if (d > 0) {
            d = Math.sqrt(d);
            double t1 = -b - d;
            if (t1 > Util.EPS) {
                return new Hit(this, t1, null);
            }

            double t2 = -b + d;
            if (t2 > Util.EPS) {
                return new Hit(this, t2, null);
            }

        }
        return Hit.NoHit;
    }

    @Override
    public Vector UV(Vector p) {
        p = p.Sub(Center);
            var u = Math.atan2(p.z, p.x);
            var v = Math.atan2(p.y, new Vector(p.x, 0, p.z).Length());
            u = 1 - (u + Math.PI) / (2 * Math.PI);
            v = (v + Math.PI / 2) / Math.PI;
            return new Vector(u, v, 0);
    }

    @Override
    public void Compile() {
    }

    @Override
    public Material MaterialAt(Vector p) {
        return Material;
    }

    @Override
    public Vector NormalAt(Vector p) {
        return p.Sub(Center).Normalize();
    }
}
