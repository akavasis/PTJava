package gopt2j;

class Triangle extends TransformedShape implements IShape {

    double INF = 1e9;
    double EPS = 1e-9;
    Material Material;
    Vector V1, V2, V3;
    Vector N1, N2, N3;
    Vector T1, T2, T3;

    static Triangle NewTriangle(Vector v1, Vector v2, Vector v3, Vector t1, Vector t2, Vector t3, Material material) {
        Triangle t = new Triangle();
        t.V1 = v1;
        t.V2 = v2;
        t.V3 = v3;
        t.T1 = t1;
        t.T2 = t2;
        t.T3 = t3;
        t.Material = material;
        t.FixNormals();
        return t;
    }

    void FixNormals() {
        Vector n = Normal();
        Vector zero = new Vector();

        if (N1.equals(zero)) {
            N1 = n;
        }

        if (N2.equals(zero)) {
            N2 = n;
        }

        if (N3.equals(zero)) {
            N3 = n;
        }
    }

    Vector Normal() {
        Vector e1 = V2.Sub(V1);
        Vector e2 = V3.Sub(V1);
        return e1.Cross(e2).Normalize();
    }

    Vector[] Vertices() {
        Vector[] ve = {V1, V2, V3};
        return ve;
    }

    @Override
    public Box BoundingBox() {
        Vector min = this.V1.Min(this.V2).Min(this.V3);
        Vector max = this.V1.Max(this.V2).Max(this.V3);
        return new Box(min, max);
    }

    @Override
    public Vector UV(Vector p) {
        double[] centric = this.Barycentric(p);
        Vector n = new Vector();
        n = n.Add(T1.MulScalar(centric[0]));
        n = n.Add(T2.MulScalar(centric[1]));
        n = n.Add(T3.MulScalar(centric[2]));

        return new Vector(n.x, n.y, 0);
    }

    double[] Barycentric(Vector p) {
        Vector v0 = V2.Sub(V1);
        Vector v1 = V3.Sub(V1);
        Vector v2 = p.Sub(V1);
        double d00 = v0.Dot(v0);
        double d01 = v0.Dot(v1);
        double d11 = v1.Dot(v1);
        double d20 = v2.Dot(v0);
        double d21 = v2.Dot(v1);
        double d = d00 * d11 - d01 * d01;
        double v = (d11 * d20 - d01 * d21) / d;
        double w = (d00 * d21 - d01 * d20) / d;
        double u = 1 - v - w;

        double[] uvw = {u, v, w};

        return uvw;

    }

    @Override
    public Material MaterialAt(Vector p) {
        return this.Material;
    }

    double Area(Triangle t) {
        Vector e1 = t.V2.Sub(t.V1);
        Vector e2 = t.V3.Sub(t.V1);
        Vector n = e1.Cross(e2);
        return n.Length() / 2;
    }

    @Override
    public Hit Intersect(Ray r) {
        var e1x = V2.x - V1.x;
        var e1y = V2.y - V1.y;
        var e1z = V2.z - V1.z;
        var e2x = V3.x - V1.x;
        var e2y = V3.y - V1.y;
        var e2z = V3.z - V1.z;
        var px = r.Direction.y * e2z - r.Direction.z * e2y;
        var py = r.Direction.z * e2x - r.Direction.x * e2z;
        var pz = r.Direction.x * e2y - r.Direction.y * e2x;
        var det = e1x * px + e1y * py + e1z * pz;

        if (det > -Util.EPS && det < Util.EPS) {
            return Hit.NoHit;
        }

        var inv = 1 / det;
        var tx = r.Origin.x - V1.x;
        var ty = r.Origin.y - V1.y;
        var tz = r.Origin.z - V1.z;
        var u = (tx * px + ty * py + tz * pz) * inv;

        if (u < 0 || u > 1) {
            return Hit.NoHit;
        }

        var qx = ty * e1z - tz * e1y;
        var qy = tz * e1x - tx * e1z;
        var qz = tx * e1y - ty * e1x;
        var v = (r.Direction.x * qx + r.Direction.y * qy + r.Direction.z * qz) * inv;

        if ((v < 0) || ((u + v) > 1)) {
            return Hit.NoHit;

        }

        var d = (e2x * qx + e2y * qy + e2z * qz) * inv;

        if (d < Util.EPS) {
            return Hit.NoHit;
        }

        return new Hit(this, d, null);
    }

    @Override
    public Vector NormalAt(Vector p) {
        double[] bcentric = this.Barycentric(p);
        double u = bcentric[0];
        double v = bcentric[1];
        double w = bcentric[2];
        Vector n = new Vector();
        n = n.Add(N1.MulScalar(u));
        n = n.Add(N2.MulScalar(v));
        n = n.Add(N3.MulScalar(w));
        n = n.Normalize();

        if (Material.NormalTexture != null) {
            Vector b = new Vector();
            b = b.Add(T1.MulScalar(u));
            b = b.Add(T2.MulScalar(v));
            b = b.Add(T3.MulScalar(w));
            Vector ns = this.Material.NormalTexture.NormalSample(b.x, b.y);
            Vector dv1 = V2.Sub(V1);
            Vector dv2 = V3.Sub(V1);
            Vector dt1 = T2.Sub(T1);
            Vector dt2 = T3.Sub(T1);
            Vector T = dv1.MulScalar(dt2.y).Sub(dv2.MulScalar(dt1.y)).Normalize();
            Vector B = dv2.MulScalar(dt1.x).Sub(dv1.MulScalar(dt2.x)).Normalize();
            Vector N = T.Cross(B);

            Matrix matrix = new Matrix(T.x, B.x, N.x, 0,
                    T.y, B.y, N.y, 0,
                    T.z, B.z, N.z, 0,
                    0, 0, 0, 1);
            matrix.MulDirection(ns);
        }

        if (this.Material.BumpTexture != null) {
            Vector b = new Vector();
            b = b.Add(T1.MulScalar(u));
            b = b.Add(T2.MulScalar(v));
            b = b.Add(T3.MulScalar(w));
            Vector bump = Material.BumpTexture.BumpSample(b.x, b.y);
            Vector dv1 = V2.Sub(V1);
            Vector dv2 = V3.Sub(V1);
            Vector dt1 = T2.Sub(T1);
            Vector dt2 = T3.Sub(T1);
            Vector tangent = dv1.MulScalar(dt2.y).Sub(dv2.MulScalar(dt1.y)).Normalize();
            Vector bitangent = dv2.MulScalar(dt1.x).Sub(dv1.MulScalar(dt2.x)).Normalize();
            n = n.Add(tangent.MulScalar(bump.x * Material.BumpMultiplier));
            n = n.Add(bitangent.MulScalar(bump.y * Material.BumpMultiplier));
        }
        n = n.Normalize();
        return n;
    }
}
