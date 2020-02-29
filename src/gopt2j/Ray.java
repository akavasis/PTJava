package gopt2j;

import gopt2j.DefaultSampler.BounceType;
import java.util.Random;
import gopt2j.Hit.HitInfo;

public class Ray {

    public Vector Origin, Direction;
    public Boolean reflected;
    public double bouncep;

    public Ray(Vector Origin, Vector Direction) {
        this.Origin = Origin;
        this.Direction = Direction;
    }

    public Ray(Vector Origin, Vector Direction, Boolean reflected, double p) {
        this.Origin = Origin;
        this.Direction = Direction;
        this.reflected = reflected;
        this.bouncep = p;
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
        double temp;

        if (info.inside) {
            temp = n1;
            n1 = n2;
            n2 = temp;
        }

        double p;

        if (material.Reflectivity >= 0) {
            p = material.Reflectivity;
        } else {
            p = n.Reflectance(this, n1, n2);
        }

        Boolean reflect = null;

        switch (bounceType) {
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
            Ray reflectedRay = n.Reflect(this);
            Ray cbounce = reflectedRay.ConeBounce(material.Gloss, u, v, rand);
            cbounce.reflected = true;
            cbounce.bouncep = p;
            return cbounce;
        } else if (material.Transparent) {
            Ray refractedRay = n.Refract(this, n1, n2);
            refractedRay.Origin = refractedRay.Origin.Add(refractedRay.Direction.MulScalar(1e-4));
            Ray rcbounce = refractedRay.ConeBounce(material.Gloss, u, v, rand);
            rcbounce.reflected = true;
            rcbounce.bouncep = 1 - p;
            return rcbounce;
        } else {
            Ray wBounce = this.WeightedBounce(u, v, rand);
            wBounce.reflected = false;
            wBounce.bouncep = 1 - p;
            return wBounce;
        }
    }
}
