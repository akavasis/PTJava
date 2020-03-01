package gopt2j;

import gopt2j.Hit.HitInfo;
import java.util.Random;

interface Sampler {

    Colour Sample(Scene scene, Ray ray, Random rand);
}

class DefaultSampler implements Sampler {

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

    static DefaultSampler NewSampler(int firstHitSamples, int maxBounces) {
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
        if (depth > MaxBounces) {
            return Colour.Black;
        }

        Hit hit = scene.Intersect(ray);

        if (!hit.Ok()) {
            return sampleEnvironment(scene, ray);
        }

        HitInfo info = hit.Info(ray);
        Material material = info.material;
        Colour result = Colour.Black;

        if (material.Emittance > 0) {
            if (DirectLighting && !emission) {
                return Colour.Black;
            }
            result = result.Add(material.Color.MulScalar(material.Emittance * (double) samples));
        }

        int n = (int) Math.sqrt(samples);
        BounceType ma, mb;

        if (SMode.equals(SpecularMode.SpecularModeAll) || (depth == 0 && SMode.equals(SpecularMode.SpecularModeFirst))) {
            ma = BounceType.BounceTypeDiffuse;
            mb = BounceType.BounceTypeSpecular;
        } else {
            ma = BounceType.BounceTypeAny;
            mb = BounceType.BounceTypeAny;
        }

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                

                for (int i = ma.ordinal(); i <= mb.ordinal(); i++) {
                    double fu = ((double) u + rand.nextDouble()) / (double) n;
                    double fv = ((double) v + rand.nextDouble()) / (double) n;

                    //(var newRay, var reflected, var p) 
                    Ray newRay = ray.Bounce(info, fu, fv, BounceType.values()[i], rand);

                    if (BounceType.values()[i] == BounceType.BounceTypeAny) {
                        //if (mode == BounceType.BounceTypeAny) {
                        newRay.bouncep = 1;
                    }

                    if (newRay.bouncep > 0 && newRay.reflected) {
                        // specular
                        Colour indirect = sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                        Colour tinted = indirect.Mix(material.Color.Mul(indirect), material.Tint);
                        result = result.Add(tinted.MulScalar(newRay.bouncep));
                    }

                    if (newRay.bouncep > 0 && !newRay.reflected) {
                        // diffuse
                        Colour indirect = sample(scene, newRay, newRay.reflected, 1, depth + 1, rand);
                        Colour direct = Colour.Black;

                        if (DirectLighting) {
                            direct = sampleLights(scene, info.Ray, rand);
                        }
                        result = result.Add(material.Color.Mul(direct.Add(indirect)).MulScalar(newRay.bouncep));
                    }
                    // mode = mb;
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

        if (LMode.equals(LightMode.LightModeAll)) {
            Colour result = new Colour();
            for (IShape light : scene.Lights) {
                if (light != null) {
                    result = result.Add(sampleLight(scene, n, rand, light));
                }

            }
            return result;

        } else {
            // pick a random light
            IShape light = scene.Lights[rand.nextInt(nLights)];
            return sampleLight(scene, n, rand, light).MulScalar(nLights);
        }
    }

    Colour sampleLight(Scene scene, Ray n, Random rand, IShape light) 
    {
        Vector center = new Vector();
        double radius = 0;
        
        if (light != null) {
            if (light instanceof Sphere) {
                radius = ((Sphere) light).Radius;
                center = ((Sphere) light).Center;
            } else {
                Box box = light.BoundingBox();
                radius = box.OuterRadius();
                center = box.Center();
            }
        }

        Vector point = center;
        
        if (SoftShadows) {
            for (;;) {
                double x = (rand.nextDouble() * 2) - 1;
                double y = (rand.nextDouble() * 2) - 1;
                if ((x * x) + (y * y) <= 1) {
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

        // construct ray toward light point
        Ray ray = new Ray(n.Origin, point.Sub(n.Origin).Normalize());
        // get cosine term
        double diffuse = ray.Direction.Dot(n.Direction);
        if (diffuse <= 0) {
            return Colour.Black;
        }
        // check for light visibility
        Hit hit = scene.Intersect(ray);
        if (!hit.Ok() || hit.Shape != light) {
            return Colour.Black;
        }
        // compute solid angle (hemisphere coverage)
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
        // get material properties from light
        Material material = Material.MaterialAt(light, point);
        // combine factors
        double m = material.Emittance * diffuse * coverage;
        return material.Color.MulScalar(m);
    }
}
