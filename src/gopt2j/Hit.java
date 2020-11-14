package gopt2j;

public class Hit {

    static final double INF = 1e9;
    IShape Shape;
    public double T;
    public HitInfo HitInfo;

    Hit(IShape shape, double t, HitInfo hinfo) {
        this.Shape = shape;
        this.T = t;
        this.HitInfo = hinfo;
    }
    
    boolean Ok() {
        return this.T < INF;
    }
    
    public static Hit NoHit = new Hit(null, INF, null);

    HitInfo Info(Ray r) {
        if (HitInfo != null)
            return HitInfo;
        
        var shape = Shape;
        var position = r.Position(T);
        var normal = shape.NormalAt(position);
        var material = Material.MaterialAt(shape, position);
        var inside = false;

        if (normal.Dot(r.Direction) > 0)
        {
            normal = normal.Negate();
            inside = true;

            if(shape instanceof Volume)
            {

            } else if(shape instanceof SDFShape)
            {

            } else if(shape instanceof SphericalHarmonic)
            inside = false;
        }
        
        Ray ray = new Ray(position, normal);
        return new HitInfo(shape, position, normal, ray, material, inside);
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
