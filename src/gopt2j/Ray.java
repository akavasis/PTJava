package gopt2j;

import java.util.Random;
import gopt2j.Hit.HitInfo;
import static gopt2j.Swap.swapDouble;

public class Ray {

    public Vector Origin, Direction;
    public boolean reflected;
    public double bouncep;
    boolean reflect;
    boolean condition;

    public Ray(Vector Origin_, Vector Direction_) {
        Origin = Origin_;
        Direction = Direction_;
    }

    public Ray(Vector Origin_, Vector Direction_, Boolean reflected_, double p_) {
        Origin = Origin_;
        Direction = Direction_;
        reflected = reflected_;
        bouncep = p_;
    }

    public Vector Position(double t) {
        return this.Origin.Add(this.Direction.MulScalar(t));
    }

    public Ray Reflect(Ray i) {
        return new Ray(this.Origin, this.Direction.Reflect(i.Direction));
    }

    public Ray Refract(Ray i, double n1, double n2) {
        return new Ray(this.Origin, this.Direction.Refract(i.Direction, n1, n2));
    }

    public double Reflectance(Ray i, double n1, double n2) {
        return this.Direction.Reflectance(i.Direction, n1, n2);
    }

    public Ray WeightedBounce(double u, double v, Random rand) {
        double radius = Math.sqrt(u);
        double theta = 2 * Math.PI * v;

        Vector s = this.Direction.Cross(Vector.RandomUnitVector(rand)).Normalize();
        Vector t = this.Direction.Cross(s);
        Vector d = new Vector();

        d = d.Add(s.MulScalar(radius * Math.cos(theta)));
        d = d.Add(t.MulScalar(radius * Math.sin(theta)));
        d = d.Add(this.Direction.MulScalar(Math.sqrt(1 - u)));

        return new Ray(this.Origin, d);
    }

    public Ray ConeBounce(double theta, double u, double v, Random rand) {
        return new Ray(this.Origin, Util.Cone(this.Direction, theta, u, v, rand));
    }

    public Ray Bounce(HitInfo info, double u, double v, BounceType bounceType, Random rand) {
        Ray n = info.Ray;
        Material material = info.material;
        double n1 = 1.0;
        double n2 = material.Index;

        if (info.inside) {
            n1 = swapDouble(n2, n2 = n1);
        }

        double p;

        if (material.Reflectivity >= 0) {
            p = material.Reflectivity;
        } else {
            p = n.Reflectance(this, n1, n2);
        }

        if (null != bounceType) {
            switch (bounceType) {
                case BounceTypeAny: {
                    reflect = rand.nextDouble() < p;
                    break;
                }
                case BounceTypeDiffuse: {
                    reflect = false;
                    break;
                }
                case BounceTypeSpecular: {
                    reflect = true;
                    break;
                }
                default:
                    break;
            }
        }

        if (reflect) {
            Ray reflected = n.Reflect(this);
            reflected.condition = true;
            reflected.bouncep = p;
            return reflected;
        } else if (material.Transparent) {
            Ray refracted = n.Refract(this, n1, n2);
            refracted.Origin = refracted.Origin.Add(refracted.Direction.MulScalar(1e-4));
            refracted.condition = true;
            return refracted;
        } else {
            Ray bounced = n.WeightedBounce(u, v, rand);
            bounced.condition = false;
            bounced.bouncep = 1 - p;
            return bounced;
        }
    }
}
