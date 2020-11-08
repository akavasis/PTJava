package gopt2j;

class Cylinder extends TransformedShape implements IShape {
    
    double INF = 1e9;
    double EPS = 1e-9;
    double Radius;
    double Z0, Z1;
    Material CylinderMaterial;
    
    Cylinder(double radius, double z0, double z1, Material material) {
        this.Radius = radius;
        this.Z0 = z0;
        this.Z1 = z1;
        this.CylinderMaterial = material;
    }
    
    Cylinder NewCylinder(double radius, double z0, double z1, Material material) {
        return new Cylinder(radius, z0, z1, material);
    }
    
    IShape NewTransformedCylinder(Vector v0, Vector v1, double radius, Material material) {
        Vector up = new Vector(0,0,1);
        Vector d = v1.Sub(v0);
        double z = d.Length();
        double a = Math.acos(d.Normalize().Dot(up));
        Matrix m = new Matrix().Translate(v0);
        if (a!=0)
        {
            Vector u = d.Cross(up).Normalize();
            m = m.Rotate(u, a).Translate(v0);
        }
        Cylinder c = NewCylinder(radius, 0, z, material);
        return NewTransformedShape(c,m);
        
    } 
    
    @Override
    public Box BoundingBox() {
        double radius = this.Radius;
        return new Box(new Vector(-radius, -radius, this.Z0), new Vector(radius, radius, this.Z1));
    }
           
    @Override
    public Vector UV(Vector p) {
        return p;
    }
    
    @Override
    public Material MaterialAt(Vector p) {
        return this.CylinderMaterial;
    }

    @Override
    public Vector NormalAt(Vector p) {
        p.z = 0;
        return p.Normalize();
    }
    
    @Override
    public Hit Intersect(Ray ray) {
        double r = this.Radius;
        Vector o = ray.Origin;
        Vector d = ray.Direction;
        double a = d.x * d.x + d.y * d.y;
        double b = 2*o.x*d.x + 2*o.y*d.y;
        double c = o.x*o.x + o.y*o.y - r*r;
        double q = b*b - 4*a*c;
        if(q < EPS )
        {
            return Hit.NoHit;
        }
        double s = Math.sqrt(q);
        double t0 = (-b + s) / (2 * a);
        double t1 = (-b - s) / (2 * a);
        if (t0 > t1)
        {
            // swap values
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }
        double z0 = o.z + t0*d.z;
        double z1 = o.z + t1*d.z;
        if (t0 > EPS && this.Z0 < z0 && z0 < this.Z1)
        {
            return new Hit(this, t0, null);
        }
        if (t1 > EPS && this.Z0 < z1 && z1 < this.Z1)
        {
            return new Hit(this, t1, null);
        }
        return Hit.NoHit;
    }
}
