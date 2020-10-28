package gopt2j;

class Cube extends TransformedShape implements IShape {

    Vector Min;
    Vector Max;
    Material Material;
    Box Box;

    Cube(Vector min, Vector max, Material material, Box box) {
        this.Min = min;
        this.Max = max;
        this.Material = material;
        this.Box = box;
    }

    static Cube NewCube(Vector min, Vector max, Material material) {
        Box box = new Box(min, max);
        return new Cube(min, max, material, box);
    }

    @Override
    public void Compile() {

    }

    @Override
    public Box BoundingBox() {
        return Box;
    }

    @Override
    public Hit Intersect(Ray r) {
        Vector n = Min.Sub(r.Origin).Div(r.Direction);
        Vector f = Max.Sub(r.Origin).Div(r.Direction);
        
        n = n.Min(f);
        f = n.Max(f);
        SwapVector(n,f);        

        var t0 = Math.max(Math.max(n.X, n.Y), n.Z);
        var t1 = Math.min(Math.min(f.X, f.Y), f.Z);

        if (t0 > 0 && t0 < t1) {
            return new Hit(this, t0, null);
        }

        return Hit.NoHit;
    }
    
    public void SwapVector(Vector a, Vector b)
    {
        Vector swap = new Vector();
        swap.X = a.X;
        swap.Y = a.Y;
        swap.Z = a.Z;
        
        a.X = b.X;
        a.Y = b.Y;
        a.Z = b.Z;
        
        b.X = swap.X;
        b.Y = swap.Y;
        b.Z = swap.Z;
        
    }

    @Override
    public Vector UV(Vector p) {
        p = p.Sub(Min).Div(Max.Sub(Min));
        return new Vector(p.X, p.Z, 0);
    }

    @Override
    public Material MaterialAt(Vector p) {
        return Material;
    }

    @Override
    public Vector NormalAt(Vector p) {
        if (p.X < this.Min.X + 1e-9) //p.X < c.Min.X+EPS:
        {
            return new Vector(-1, 0, 0);  //Vector{-1, 0, 0}
        } else if (p.X > this.Max.X - 1e-9) //p.X > c.Max.X-EPS:
        {
            return new Vector(1, 0, 0);   //return Vector{1, 0, 0}
        } else if (p.Y < this.Min.Y + 1e-9) //p.Y < c.Min.Y+EPS:
        {
            return new Vector(0, -1, 0);  //return Vector{0, -1, 0}
        } else if (p.Y > this.Max.Y - 1e-9) //p.Y > c.Max.Y-EPS:
        {
            return new Vector(0, 1, 0);   //return Vector{0, 1, 0}
        } else if (p.Z < this.Min.Z + 1e-9) //p.Z < c.Min.Z+EPS:
        {
            return new Vector(0, 0, -1);  //return Vector{0, 0, -1}
        } else if (p.Z > this.Max.Z - 1e-9) //p.Z > c.Max.Z-EPS:
        {
            return new Vector(0, 0, 1);   //return Vector{0, 0, 1}     
        } else {
            return new Vector(0, 1, 0);     //return Vector{0, 1, 0}
        }

    }

    Mesh Mesh() {
        Vector a = Min;
        Vector b = Max;
        Vector z = new Vector();
        Material m = Material;
        var v000 = new Vector(a.X, a.Y, a.Z);
        var v001 = new Vector(a.X, a.Y, b.Z);
        var v010 = new Vector(a.X, b.Y, a.Z);
        var v011 = new Vector(a.X, b.Y, b.Z);
        var v100 = new Vector(b.X, a.Y, a.Z);
        var v101 = new Vector(b.X, a.Y, b.Z);
        var v110 = new Vector(b.X, b.Y, a.Z);
        var v111 = new Vector(b.X, b.Y, b.Z);

        Triangle[] triangles = {
            Triangle.NewTriangle(v000, v100, v110, z, z, z, m),
            Triangle.NewTriangle(v000, v110, v010, z, z, z, m),
            Triangle.NewTriangle(v001, v101, v111, z, z, z, m),
            Triangle.NewTriangle(v001, v111, v011, z, z, z, m),
            Triangle.NewTriangle(v000, v100, v101, z, z, z, m),
            Triangle.NewTriangle(v000, v101, v001, z, z, z, m),
            Triangle.NewTriangle(v010, v110, v111, z, z, z, m),
            Triangle.NewTriangle(v010, v111, v011, z, z, z, m),
            Triangle.NewTriangle(v000, v010, v011, z, z, z, m),
            Triangle.NewTriangle(v000, v011, v001, z, z, z, m),
            Triangle.NewTriangle(v100, v110, v111, z, z, z, m),
            Triangle.NewTriangle(v100, v111, v101, z, z, z, m)
        };
        return new Mesh(triangles, null, null);
    }
}
