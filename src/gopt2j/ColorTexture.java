package gopt2j;

import java.util.Arrays;

interface Texture {

    Colour Sample(double u, double v);

    Vector NormalSample(double u, double v);

    Vector BumpSample(double u, double v);

    Texture Pow(double a);

    Texture MulScalar(double a);
}

class ColorTexture implements Texture {

    int Width;
    int Height;
    Colour[] Data;

    double INF = 1e9;
    double EPS = 1e-9;

    ColorTexture() {
        this.Width = 0;
        this.Height = 0;
        this.Data = new Colour[Height * Width + Width];
        Arrays.fill(this.Data, new Colour(0, 0, 0));
    }

    ColorTexture(int width, int height, Colour[] data) {
        this.Width = width;
        this.Height = height;
        this.Data = data;
    }

    @Override
    public Colour Sample(double u, double v) {
        u = Util.Fract((Util.Fract(u)) + 1);
        v = Util.Fract((Util.Fract(v)) + 1);
        return this.bilinearSample(u, 1 - v);
    }

    Colour bilinearSample(double u, double v) {
        if (u == 1) {
            u -= EPS;
        }

        if (v == 1) {
            v -= EPS;
        }

        double w = this.Width - 1;
        double h = this.Height - 1;
        int X, Y, x0, y0, x1, y1;
        double x, y;
        X = (int) (u * w);
        Y = (int) (v * h);
        x = Util.Fract(u * w);
        y = Util.Fract(v * h);
        x0 = (int) (X);
        y0 = (int) (Y);
        x1 = x0 + 1;
        y1 = y0 + 1;
        Colour c00 = this.Data[y0 * this.Width + x0];
        Colour c01 = this.Data[y1 * this.Width + x0];
        Colour c10 = this.Data[y0 * this.Width + x1];
        Colour c11 = this.Data[y1 * this.Width + x1];
        Colour c = new Colour(0, 0, 0);
        c = c.Add(c00.MulScalar((1 - x) * (1 - y)));
        c = c.Add(c10.MulScalar(x * (1 - y)));
        c = c.Add(c01.MulScalar((1 - x) * y));
        c = c.Add(c11.MulScalar(x * y));
        return c;
    }

    @Override
    public Vector BumpSample(double u, double v) {
        u = Util.Fract(Util.Fract(u) + 1);
        v = Util.Fract(Util.Fract(v) + 1);
        v = 1 - v;
        int x = (int) (u * this.Width);
        int y = (int) (v * this.Height);
        int x1 = Util.ClampInt(x - 1, 0, this.Width - 1);
        int x2 = Util.ClampInt(x + 1, 0, this.Height - 1);
        int y1 = Util.ClampInt(y - 1, 0, this.Height - 1);
        int y2 = Util.ClampInt(y + 1, 0, this.Height - 1);
        Colour cx = this.Data[y * this.Width + x].Sub(this.Data[y * this.Width + 2]);
        Colour cy = this.Data[y1 * this.Width + x].Sub(this.Data[y2 * this.Width + x]);
        return new Vector(cx.r, cy.r, 0);
    }

    @Override
    public Texture Pow(double a) {
        for (int i = 0; i < this.Data.length; i++) {
            this.Data[i] = this.Data[i].Pow(a);
            //t.Data[i] = t.Data[i].Pow(a);
        }
        return this;
    }

    @Override
    public Texture MulScalar(double a) {
        for (int i = 0; i < this.Data.length; i++) {
            this.Data[i] = this.Data[i].MulScalar(a);
            //t.Data[i] = t.Data[i].MulScalar(a);
        }
        return this;
    }

    @Override
    public Vector NormalSample(double u, double v) {
        Colour c = this.Sample(u, v);
        return new Vector(c.r * 2 - 1, c.g * 2 - 1, c.b * 2 - 1).Normalize();
    }
}
