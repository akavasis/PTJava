package gopt2j;

import gopt2j.Hit.HitInfo;
import java.util.Random;

interface Sampler {

    Colour Sample(Scene scene, Ray ray, Random rand);
}

class DefaultSampler implements Sampler {

    public static enum LightMode {
        LightModeRandom, LightModeAll
    }

    public static enum SpecularMode {
        SpecularModeNaive, SpecularModeFirst, SpecularModeAll
    }

    public static enum BounceType {
        BounceTypeAny, BounceTypeDiffuse, BounceTypeSpecular
    }

    int FirstHitSamples;
    int MaxBounces;
    boolean DirectLighting;
    boolean SoftShadows;
    public LightMode LMode;
    public SpecularMode SMode;

    DefaultSampler() {

    }

    DefaultSampler(int FirstHitSamples, int MaxBounces, boolean DirectLighting, boolean SoftShadows, LightMode LM, SpecularMode SM) {
        this.FirstHitSamples = FirstHitSamples;
        this.MaxBounces = MaxBounces;
        this.DirectLighting = DirectLighting;
        this.SoftShadows = SoftShadows;
        this.LMode = LM;
        this.SMode = SM;
    }

    DefaultSampler NewSampler(int firstHitSamples, int maxBounces) {
        return new DefaultSampler(firstHitSamples, maxBounces, true, true, LightMode.LightModeRandom, SpecularMode.SpecularModeNaive);
    }

    DefaultSampler NewDirectSampler() {
        return new DefaultSampler(1, 0, true, false, LightMode.LightModeAll, SpecularMode.SpecularModeAll);
    }

    @Override
    public Colour Sample(Scene scene, Ray ray, Random rand) {

        return this.sample(scene, ray, true, this.FirstHitSamples, 0, rand);
    }

    Colour sample(Scene scene, Ray ray, boolean emission, int samples, int depth, Random rand) {

        if (depth > this.MaxBounces) {
            return Colour.Black;
        }

        Hit hit = scene.Intersect(ray);

        if (!hit.Ok()) {
            return this.sampleEnvironment(scene, ray);

        }

        HitInfo info = hit.Info(ray);
        Material material = info.material;
        Colour result = new Colour(0, 0, 0);

        if (material.Emittance > 0) {
            if (this.DirectLighting && !emission) {
                return new Colour(0, 0, 0);
            }
            result = result.Add(material.Color.MulScalar(material.Emittance * (double) samples));
        }

        int n = (int) (Math.sqrt((double) (samples)));

        BounceType ma, mb;

        if ((SMode == SpecularMode.SpecularModeAll) || (depth == 0 && SMode == SpecularMode.SpecularModeFirst)) {
            ma = BounceType.BounceTypeDiffuse;
            mb = BounceType.BounceTypeSpecular;
        } else {
            ma = BounceType.BounceTypeAny;
            mb = BounceType.BounceTypeAny;
        }

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {

                if ((ma == BounceType.BounceTypeAny) && (mb == BounceType.BounceTypeAny)) {
                    double fu = ((double) u + rand.nextDouble()) / (double) n;
                    double fv = ((double) v + rand.nextDouble()) / (double) n;

                    Ray newRay = ray.Bounce(info, fu, fv, BounceType.BounceTypeAny, rand);
                    newRay.bouncep = 1;

                    if (newRay.bouncep > 0 && newRay.reflected) {
                        // specular
                        Colour indirect = this.sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                        Colour tinted = indirect.Mix(material.Color.Mul(indirect), material.Tint);
                        result = result.Add(tinted.MulScalar(newRay.bouncep));
                    }

                    if (newRay.bouncep > 0 && !newRay.reflected) {
                        // diffuse
                        Colour indirect = this.sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                        Colour direct = new Colour(0, 0, 0);

                        if (this.DirectLighting) {
                            direct = this.sampleLights(scene, info.Ray, rand);
                        }

                        result = result.Add(material.Color.Mul(direct.Add(indirect)).MulScalar(newRay.bouncep));
                    }

                }

                if ((ma == BounceType.BounceTypeDiffuse) && (mb == BounceType.BounceTypeSpecular)) {

                    double fu = ((double) u + rand.nextDouble()) / (double) n;
                    double fv = ((double) v + rand.nextDouble()) / (double) n;

                    Ray newRay = null;

                    for (int i = 1; i <= 2; i++) {

                        if (i == 1) {
                            newRay = ray.Bounce(info, fu, fv, BounceType.BounceTypeDiffuse, rand);
                        } else if (i == 2) {
                            newRay = ray.Bounce(info, fu, fv, BounceType.BounceTypeSpecular, rand);
                        }

                        if (newRay.bouncep > 0 && newRay.reflected) {
                            // specular
                            Colour indirect = this.sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                            Colour tinted = indirect.Mix(material.Color.Mul(indirect), material.Tint);
                            result = result.Add(tinted.MulScalar(newRay.bouncep));
                        }

                        if (newRay.bouncep > 0 && !newRay.reflected) {
                            // diffuse
                            Colour indirect = this.sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                            Colour direct = new Colour(0, 0, 0);

                            if (this.DirectLighting) {
                                direct = this.sampleLights(scene, info.Ray, rand);
                            }

                            result = result.Add(material.Color.Mul(direct.Add(indirect)).MulScalar(newRay.bouncep));
                        }
                    }

                }

            }
        }

        return result.DivScalar((double) (n * n));
    }

    Colour sampleEnvironment(Scene scene, Ray ray) {
        if (scene.Texture != null) {
            Vector d = ray.Direction;
            double u = Math.atan2(d.Z, d.X) + scene.TextureAngle;
            double v = Math.atan2(d.Y, new Vector(d.X, 0, d.Z).Length());
            u = (u + Math.PI) / (2 * Math.PI);
            v = (v + Math.PI / 2) / Math.PI;
            return scene.Texture.Sample(u, v);
        }
        return scene.Color;
    }

    Colour sampleLights(Scene scene, Ray n, Random rand) {
        int nLights = scene.Lights.length;
        if (nLights == 0) {
            return Colour.Black;
        }
        if (LMode == LightMode.LightModeAll) {
            Colour result = new Colour();
            for (IShape light : scene.Lights) {
                result = result.Add(this.sampleLight(scene, n, rand, light));
            }
            return result;
        } else {
            IShape light = scene.Lights[rand.nextInt(nLights)];
            return this.sampleLight(scene, n, rand, light).MulScalar((double) nLights);
        }
    }

    Colour sampleLight(Scene scene, Ray n, Random rand, IShape light) {
        Vector center;
        double radius;
        if (light instanceof Sphere) {
            radius = ((Sphere) light).Radius;
            center = ((Sphere) light).Center;
        } else {
            Box box = light.BoundingBox();
            radius = box.OuterRadius();
            center = box.Center();
        }
        Vector point = center;
        if (this.SoftShadows) {
            for (;;) {
                double x = rand.nextDouble() * 2 - 1;
                double y = rand.nextDouble() * 2 - 1;
                if (x * x + y * y <= 1) {
                    Vector l = center.Sub(n.Origin).Normalize();
                    Vector u = l.Cross(Vector.RandomUnitVector(rand)).Normalize();
                    Vector v = l.Cross(u);
                    point = new Vector();
                    point = point.Add(u.MulScalar(x * radius));
                    point = point.Add(v.MulScalar(y * radius));
                    point = point.Add(center);
                    break;
                }
            }
        }
        Ray ray = new Ray(n.Origin, point.Sub(n.Origin).Normalize());
        double diffuse = ray.Direction.Dot(n.Direction);
        if (diffuse <= 0) {
            return Colour.Black;
        }
        Hit hit = scene.Intersect(ray);
        if (!hit.Ok() || hit.Shape != light) {
            return Colour.Black;
        }
        double hyp = center.Sub(n.Origin).Length();
        double opp = radius;
        double theta = Math.asin(opp / hyp);
        double adj = opp / Math.tan(theta);
        double d = Math.cos(theta) * adj;
        double r = Math.sin(theta) * adj;
        double coverage = (r * r) / (d * d);
        if (hyp < opp) {
            coverage = 1;
        }
        coverage = Math.min(coverage, 1);
        Material material = Material.MaterialAt(light, point);
        double m = material.Emittance * diffuse * coverage;
        return material.Color.MulScalar(m);
    }
}
