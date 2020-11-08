package gopt2j;

class Volume extends TransformedShape implements IShape {

    public class VolumeWindow {
        double Lo, Hi;
        Material Material;

        VolumeWindow() {
            this.Lo = 0;
            this.Hi = 0;
            this.Material = null;
        }

        VolumeWindow(double Lo, double Hi, Material Material) {
            this.Lo = Lo;
            this.Hi = Hi;
            this.Material = Material;
        }

    }

    int W, H, D;
    double ZScale;
    double[] Data;
    VolumeWindow[] Windows;
    Box Box;

    Volume() {
        this.W = 0;
        this.H = 0;
        this.D = 0;
        this.ZScale = 0;
        this.Data = null;
        this.Windows = null;
        this.Box = null;
    }

    Volume(int W, int H, int D, double ZScale, double[] Data, VolumeWindow[] Windows, Box Box) {
        this.W = W;
        this.H = H;
        this.D = D;
        this.ZScale = ZScale;
        this.Data = Data;
        this.Windows = Windows;
        this.Box = Box;
    }

    double Get(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= this.W || y >= this.H || z >= this.D) {
            return 0;
        }

        return this.Data[x + y * this.W + z * this.W * this.H];

    }

    Volume NewVolume(Box box, Image[] images, double sliceSpacing, VolumeWindow[] windows) {
        int w = images[0].GetWidth();
        int h = images[0].GetHeight();
        int d = images.length;

        // w/g aspect ratio TODO
        double zs = (sliceSpacing * (double) d) / (double) w;
        for (Image image : images) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    /*
                    r, _, _, _ := im.At(x, y).RGBA()
				f := float64(r) / 65535
				data[x+y*w+z*w*h] = f
                     */
                }
            }
        }
        return new Volume(w, h, d, zs, null, windows, box);
    }

    double Sample(double x, double y, double z) {
        z = z / this.ZScale;
        x = ((x + 1) / 2) * (double) this.W;
        y = ((z + 1) / 2) * (double) this.H;
        z = ((z + 2) / 2) * (double) this.D;
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        int z0 = (int) Math.floor(z);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;
        double v000 = this.Get(x0, y0, z0); //v000 := v.Get(x0, y0, z0)
        double v001 = this.Get(x0, y0, z1); //v001 := v.Get(x0, y0, z1)
        double v010 = this.Get(x0, y1, z0); //v010 := v.Get(x0, y1, z0)
        double v011 = this.Get(x0, y1, z1); //v011 := v.Get(x0, y1, z1)
        double v100 = this.Get(x1, y0, z0); //v100 := v.Get(x1, y0, z0)
        double v101 = this.Get(x1, y0, z1); //v101 := v.Get(x1, y0, z1)
        double v110 = this.Get(x1, y1, z0); //v110 := v.Get(x1, y1, z0)
        double v111 = this.Get(x1, y1, z1); //v111 := v.Get(x1, y1, z1)
        x = x - (double) x0;
        y = y - (double) y0;
        z = z - (double) z0;
        double c00 = v000 * (1 - x) + v100 * x;
        double c01 = v001 * (1 - x) + v101 * x;
        double c10 = v010 * (1 - x) + v110 * x;
        double c11 = v011 * (1 - x) + v111 * x;
        double c0 = c00 * (1 - y) + c10 * y;
        double c1 = c01 * (1 - y) + c11 * y;
        double c = c0 * (1 - z) + c1 * z;
        return c;
    }

    @Override
    public Box BoundingBox() {
        return Box;
    }

    @Override
    public void Compile() {
    }

    int Sign(Vector a) {
        double s = this.Sample(a.x, a.y, a.z);
        for (VolumeWindow win : this.Windows) {
            if (s < win.Lo) {
                return win.hashCode() + 1;
            }
            if (s > win.Hi) {
                continue;
            }
            return 0;
        }
        return this.Windows.length + 1;
    }

    @Override
    public Vector UV(Vector p) {
        return new Vector();
    }

    @Override
    public Vector NormalAt(Vector p) {
        double eps = 0.001;
        Vector n = new Vector(this.Sample(p.x - eps, p.y, p.z) - this.Sample(p.x + eps, p.y, p.z),
                this.Sample(p.x, p.y - eps, p.z) - this.Sample(p.x, p.y + eps, p.z),
                this.Sample(p.x, p.y, p.z - eps) - this.Sample(p.x, p.y, p.z + eps));
        return n.Normalize();
    }

    @Override
    public Material MaterialAt(Vector p) {
        double be = 1e9;
        Material bm = null;
        double s = this.Sample(p.x, p.y, p.z);
        for (VolumeWindow Window : this.Windows) {
            if (s >= Window.Lo && s <= Window.Hi) {
                return Window.Material;
            }
            double e = Math.min(Math.abs(s - Window.Lo), Math.abs(s - Window.Hi));
            if (e < be) {
                be = e;
                bm = Window.Material;
            }
        }
        return bm;
    }

    @Override
    public Hit Intersect(Ray ray) {
        Double[] tbool = Box.Intersect(ray);
        var tmin = tbool[0];
        var tmax = tbool[1];
        double step = 1.0 / 512;
        double start = Math.max(step, tmin);
        int sign = -1;
        for (double t = start; t <= tmax; t += step) {
            Vector p = ray.Position(t);
            int s = this.Sign(p);
            if (s == 0 || (sign >= 0 && s != sign)) {
                t -= step;
                step /= 64;
                t += step;
                for (int i = 0; i < 64; i++) {
                    if (this.Sign(ray.Position(t)) == 0) {
                        return new Hit(this, t - step, null);
                    }
                }
            }
            sign = s;
        }
        return Hit.NoHit;
    }
}
