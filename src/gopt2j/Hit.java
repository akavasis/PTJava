package gopt2j;

public class Hit {

    static final double INF = 1e9;
    IShape Shape;
    public double T;
    public HitInfo HitInfo;

    public static Hit NoHit = new Hit(null, INF, null);

    Hit(IShape shape, double t, HitInfo hinfo) {
        this.Shape = shape;
        this.T = t;
        this.HitInfo = hinfo;
    }

    HitInfo Info(Ray r) {

        if (this.HitInfo != null) {
            return this.HitInfo;
        }

        IShape shape = this.Shape;
        Vector position = r.Position(this.T);
        Vector normal = this.Shape.NormalAt(position);
        Material material = Material.MaterialAt(this.Shape, normal);
        Boolean inside = false;

        if (normal.Dot(r.Direction) > 0) {
            normal = normal.Negate();
            inside = true;
            if (shape instanceof Volume) {
                inside = false;
            }
        }

        Ray ray = new Ray(position, normal);
        return new HitInfo(Shape, position, normal, ray, material, inside);
    }

    boolean Ok() {
        return this.T < INF;
    }

    public static class HitInfo {

        IShape shape;
        Vector position;
        Vector normal;
        Ray Ray;
        Material material;
        boolean inside;

        HitInfo(IShape shape, Vector position, Vector normal, Ray r, Material mat, Boolean inside) {
            this.shape = shape;
            this.position = position;
            this.normal = normal;
            this.Ray = r;
            this.material = mat;
            this.inside = inside;
        }
    }

}
