package gopt2j;

class Cube extends TransformedShape implements IShape {

    Vector Min;
    Vector Max;
    Material Material;
    Box Box;
    
    static double EPS = 1e-9;

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
        
        var nf = Tuple.valueOf(n.Min(f), n.Max(f));
        
        n = nf._0;
        f = nf._1;

        var t0 = Math.max(Math.max(n.x, n.y), n.z);
        var t1 = Math.min(Math.min(f.x, f.y), f.z);
        return t0 > 0 && t0 < t1 ? new Hit(this, t0, null) : Hit.NoHit;
    }
    
    public void SwapVector(Vector a, Vector b)
    {
        Vector swap = new Vector();
        swap.x = a.x;
        swap.y = a.y;
        swap.z = a.z;
        
        a.x = b.x;
        a.y = b.y;
        a.z = b.z;
        
        b.x = swap.x;
        b.y = swap.y;
        b.z = swap.z;
        
    }

    @Override
    public Vector UV(Vector p) {
        p = p.Sub(Min).Div(Max.Sub(Min));
        return new Vector(p.x, p.z, 0);
    }

    @Override
    public Material MaterialAt(Vector p) {
        return Material;
    }

    @Override
    public Vector NormalAt(Vector p) {
        
       
        if (p.x < Min.x + EPS) //p.X < c.Min.X+EPS:
        {
            return new Vector(-1, 0, 0); //Vector{-1, 0, 0}
        } 
        
        else if (p.x > Max.x - EPS) //p.X > c.Max.X-EPS:
        {
            return new Vector(1, 0, 0);   //return Vector{1, 0, 0}
        } 
        
        else if (p.y < this.Min.y + EPS) //p.Y < c.Min.Y+EPS:
        {
            return new Vector(0, -1, 0);  //return Vector{0, -1, 0}
        } 
        
        else if (p.y > this.Max.y - EPS) //p.Y > c.Max.Y-EPS:
        {
            return new Vector(0, 1, 0);   //return Vector{0, 1, 0}
        } 
        
        else if (p.z < this.Min.z + EPS) //p.Z < c.Min.Z+EPS:
        {
            return new Vector(0, 0, -1);  //return Vector{0, 0, -1}
        } 
        
        else if (p.z > this.Max.z - EPS) //p.Z > c.Max.Z-EPS:
        {
            return new Vector(0, 0, 1);   //return Vector{0, 0, 1}     
        } 
        
        return new Vector(0, 1, 0);     //return Vector{0, 1, 0}
    }

    Mesh Mesh() {
        Vector a = Min;
        Vector b = Max;
        Vector z = new Vector();
        Material m = Material;
        var v000 = new Vector(a.x, a.y, a.z);
        var v001 = new Vector(a.x, a.y, b.z);
        var v010 = new Vector(a.x, b.y, a.z);
        var v011 = new Vector(a.x, b.y, b.z);
        var v100 = new Vector(b.x, a.y, a.z);
        var v101 = new Vector(b.x, a.y, b.z);
        var v110 = new Vector(b.x, b.y, a.z);
        var v111 = new Vector(b.x, b.y, b.z);

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
