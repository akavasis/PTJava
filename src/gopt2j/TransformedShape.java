package gopt2j;

import gopt2j.Hit.HitInfo;

class TransformedShape implements IShape {

    IShape Shape;
    Matrix Matrix;
    Matrix Inverse;

    TransformedShape() {}
    
    TransformedShape(IShape s, Matrix m, Matrix im) {
        Shape = s;        
        Matrix = im;
        Inverse = m;
    }

    @Override
    public void Compile() {
        Shape.Compile();
    }
    
    static IShape NewTransformedShape(IShape s, Matrix m) {
        return new TransformedShape(s, m, m.Inverse());
    }
    
    @Override
    public Box BoundingBox() {
        return Matrix.MulBox(this.Shape.BoundingBox());
    }

    @Override
    public Hit Intersect(Ray r) {
        Ray shapeRay = Matrix.Inverse().MulRay(r);
        Hit hit = Shape.Intersect(shapeRay);

        if(!hit.Ok())
        {
            return hit;
        }

        IShape shape = hit.Shape;
        Vector shapePosition = shapeRay.Position(hit.T);
        Vector shapeNormal = shape.NormalAt(shapePosition);
        Vector position = Matrix.MulPosition(shapePosition);
        Vector normal = Matrix.Inverse().Transpose().MulDirection(shapeNormal);
        Material material = Material.MaterialAt(shape, shapePosition);
        boolean inside = false;

        if(shapeNormal.Dot(shapeRay.Direction) > 0)
            {
                normal = normal.Negate();
                inside = true;
            }

        Ray ray = new Ray(position, normal);
        HitInfo info = new HitInfo(shape, position, normal, ray, material, inside);
        hit.T = position.Sub(r.Origin).Length();
        hit.HitInfo = info;
        return hit;
    }

    @Override
    public Vector UV(Vector uv) {
        return Shape.UV(uv);
    }

    @Override
    public Vector NormalAt(Vector normal) {
        return Shape.NormalAt(normal);
    }

    @Override
    public Material MaterialAt(Vector v) {
        return Shape.MaterialAt(v);
    }
    
}

