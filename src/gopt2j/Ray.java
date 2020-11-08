package gopt2j;

import java.util.Random;
import gopt2j.Hit.HitInfo;

public class Ray {

    public Vector Origin, Direction;
    public boolean reflected;
    public double p;
    boolean reflect;
    boolean condition;

    public Ray(Vector Origin_, Vector Direction_) {
        Origin = Origin_;
        Direction = Direction_;
    }

    public Vector Position(double t) {
        return Origin.Add(Direction.MulScalar(t));
    }

    public Ray Reflect(Ray i) {
        return new Ray(Origin, Direction.Reflect(i.Direction));
    }

    public Ray Refract(Ray i, double n1, double n2) {
        return new Ray(Origin, Direction.Refract(i.Direction, n1, n2));
    }

    public double Reflectance(Ray i, double n1, double n2) {
        return Direction.Reflectance(i.Direction, n1, n2);
    }

    public Ray WeightedBounce(double u, double v, Random rand) {
        var radius = Math.sqrt(u);
        var theta = 2 * Math.PI * v;
        var s = Direction.Cross(Vector.RandomUnitVector(rand)).Normalize();
        var t = Direction.Cross(s);
        var d = new Vector();
        d = d.Add(s.MulScalar(radius * Math.cos(theta)));
        d = d.Add(t.MulScalar(radius * Math.sin(theta)));
        d = d.Add(Direction.MulScalar(Math.sqrt(1 - u)));
        return new Ray(Origin, d);
    }

    public Ray ConeBounce(double theta, double u, double v, Random rand) {
        return new Ray(Origin, Util.Cone(Direction, theta, u, v, rand));
    }

    public Tuple3 Bounce(HitInfo info, double u, double v, BounceType bounceType, Random rand) {
        Ray n = info.Ray;
        Material material = info.material;

        double n1 = 1.0;
        double n2 = material.Index;

        if (info.inside) {
            double swap = n1;
            n1 = n2;
            n2 = swap;           
        }

        double p = material.Reflectivity >= 0 ? material.Reflectivity : n.Reflectance(this, n1, n2);

        
        if (null != bounceType) switch (bounceType) {
            case BounceTypeAny:
                reflect = rand.nextDouble() < p;
                break;
            case BounceTypeDiffuse:
                reflect = false;
                break;
            case BounceTypeSpecular:
                reflect = true;
                break;
        }

        if (reflect) {
            var reflected = n.Reflect(this);
            //reflected.condition = true;
            //reflected.p = p;
            //return reflected.ConeBounce(material.Gloss, u, v, rand);
            //return new Tuple3<>(reflected.ConeBounce(material.Gloss, u, v, rand), true, p);
            return Tuple.valueOf(reflected.ConeBounce(material.Gloss, u, v, rand), true, p);
            
        } else if (material.Transparent) {
            var refracted = n.Refract(this, n1, n2);
            //refracted.Origin = refracted.Origin.Add(refracted.Direction.MulScalar(1e-4));
            //refracted.condition = true;
            //refracted.p = 1 - p;
            //return new Tuple3<>(refracted.ConeBounce(material.Gloss, u, v, rand), true, 1-p);
            return Tuple.valueOf(refracted.ConeBounce(material.Gloss, u, v, rand), true, 1-p);
        } else {
            //var bounced = n.WeightedBounce(u, v, rand);
            //bounced.condition = false;
            //bounced.p = 1 - p;
            //return new Tuple3<>(n.WeightedBounce(u, v, rand), false, 1 - p);
            return Tuple.valueOf(n.WeightedBounce(u, v, rand), false, 1 - p);
        }
    }
}
