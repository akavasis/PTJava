package gopt2j;

interface Func extends IShape {

    double func(double x, double y);
}

class Function implements Func {

    Func Function;
    Box Box;
    Material Material;

    Function() {
    }

    Function(Func Function, Box Box, Material Material) {
        this.Function = Function;
        this.Box = Box;
        this.Material = Material;
    }

    IShape NewFunction(Func function, Box box, Material material) {
        return new Function(function, box, material);
    }

    @Override
    public void Compile() {
    }

    @Override
    public Box BoundingBox() {
        return this.Box;
    }

    boolean Contains(Vector v) {
        return v.Z < Function.func(v.X, v.Y);
    }

    @Override
    public Hit Intersect(Ray ray) {
        double step = 1.0 / 32;
        boolean sign = Contains(ray.Position(step));
        for (double t = step; t < 12; t += step) {
            Vector v = ray.Position(t);
            if (Contains(v) != sign && Box.Contains(v)) {
                return new Hit(this, t - step, null);
            }
        }
        return Hit.NoHit;
    }

    @Override
    public Vector UV(Vector p) {
        double x1 = Box.Min.X;
        double x2 = Box.Max.X;
        double y1 = Box.Min.Y;
        double y2 = Box.Max.Y;
        double u = p.X - x1 / x2 - x1;
        double v = p.Y - y1 / y2 - y1;
        return new Vector(u, v, 0);
    }

    @Override
    public Material MaterialAt(Vector p) {
        return this.Material;
    }

    @Override
    public Vector NormalAt(Vector p) {
        double eps = 1e-3;
        double x = Function.func(p.X - eps, p.Y) - Function.func(p.X + eps, p.Y);
        double y = Function.func(p.X, p.Y - eps) - Function.func(p.X, p.Y + eps);
        double z = 2 * eps;
        Vector v = new Vector(x, y, z);
        return v.Normalize();
    }

    @Override
    public double func(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
