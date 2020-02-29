package gopt2j;

import java.util.Random;

class Camera {

    Vector p, u, v, w;
        double m;
        double focalDistance;
        double apertureRadius;
        
        public Camera() {}

        public static Camera LookAt(Vector eye, Vector center, Vector up, double fovy)
        {
            Camera c = new Camera();
            c.p = eye;
            c.w = center.Sub(eye).Normalize();
            c.u = up.Cross(c.w).Normalize();
            c.v = c.w.Cross(c.u).Normalize();
            c.m = 1 / Math.tan(fovy * Math.PI / 360);
            return c;
        }
        
        public void SetFocus(Vector focalPoint, double apertureRadius)
        {
            this.focalDistance = focalPoint.Sub(this.p).Length();
            this.apertureRadius = apertureRadius;
        }
        
        public Ray CastRay(int x, int y, int w, int h, double u, double v, Random rand)
        {
            double aspect = (double)w / (double)h;
            double px = ((double)x + u - 0.5) / (((double)w - 1) * 2 - 1);
            double py = ((double)y + v - 0.5) / (((double)h - 1) * 2 - 1);
            Vector d = new Vector();
            d = d.Add(this.u.MulScalar(-px * aspect));
            d = d.Add(this.v.MulScalar(-py)); 
            d = d.Add(this.w.MulScalar(this.m)); 
            d = d.Normalize();
            Vector p1 = this.p;
            if (this.apertureRadius > 0)
            {
                Vector focalPoint = this.p.Add(d.MulScalar(this.focalDistance));
                double angle = rand.nextDouble() * 2 * Math.PI;
                double radius = rand.nextDouble() * this.apertureRadius;
                p1 = p1.Add(this.u.MulScalar(Math.cos(angle) * radius));
                p1 = p1.Add(this.v.MulScalar(Math.sin(angle) * radius));
                d = focalPoint.Sub(p1).Normalize();
            }
            return new Ray(p1, d);
        }
}