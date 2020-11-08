package gopt2j;

import java.util.Random;

public class Vector {

    public static Vector ORIGIN = new Vector(0, 0, 0);
    public double x, y, z;

    public Vector() {
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector RandomUnitVector(Random rnd) {
        for (;;) {
            double x, y, z;
            if (rnd == null) {
                x = new Random().nextDouble() * 2 - 1;
                y = new Random().nextDouble() * 2 - 1;
                z = new Random().nextDouble() * 2 - 1;
            } else {
                x = rnd.nextDouble() * 2 - 1;
                y = rnd.nextDouble() * 2 - 1;
                z = rnd.nextDouble() * 2 - 1;
            }
            if (x * x + y * y + z * z > 1) {
                continue;
            }
            return new Vector(x, y, z).Normalize();
        }
    }

    public double Length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double LengthN(double n) {
        if (n == 2) {
            return this.Length();
        }
        Vector a = this.Abs();
        return Math.pow(Math.pow(a.x, n) + Math.pow(a.y, n) + Math.pow(a.z, n), 1 / n);
    }

    public double Dot(Vector b) {
        return this.x * b.x + this.y * b.y + this.z * b.z;
    }

    public Vector Cross(Vector b) {
        return new Vector(this.y * b.z - this.z * b.y,
                this.z * b.x - this.x * b.z,
                this.x * b.y - this.y * b.x);
    }

    public Vector Normalize() {
        double d = this.Length();
        return new Vector(this.x / d, this.y / d, this.z / d);
    }

    public Vector Negate() {
        return new Vector(-this.x, -this.y, -this.z);
    }

    Vector Abs() {
        return new Vector(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vector Add(Vector b) {
        return new Vector(this.x + b.x, this.y + b.y, this.z + b.z);
    }

    public Vector Sub(Vector b) {
        return new Vector(this.x - b.x, this.y - b.y, this.z - b.z);
    }

    public Vector Mul(Vector b) {
        return new Vector(this.x * b.x, this.y * b.y, this.z * b.z);
    }

    public Vector Div(Vector b) {
        return new Vector(this.x / b.x, this.y / b.y, this.z / b.z);
    }

    public Vector Mod(Vector b) {
        return new Vector(this.x - b.x * Math.floor(this.x / b.x),
                this.y - b.y * Math.floor(this.y / b.y),
                this.z - b.z * Math.floor(this.z / b.z));
    }

    public Vector AddScalar(double b) {
        return new Vector(this.x + b, this.y + b, this.z + b);
    }

    public Vector SubScalar(double b) {
        return new Vector(this.x - b, this.y - b, this.z - b);
    }

    public Vector MulScalar(double b) {
        return new Vector(this.x * b, this.y * b, this.z * b);
    }

    public Vector DivScalar(double b) {
        return new Vector(this.x / b, this.y / b, this.z / b);
    }

    public Vector Min(Vector b) {
        return new Vector(Math.min(this.x, b.x), Math.min(this.y, b.y), Math.min(this.z, b.z));
    }

    public Vector Max(Vector b) {
        return new Vector(Math.max(this.x, b.x), Math.max(this.y, b.y), Math.max(this.z, b.z));
    }

    public Vector MinAxis() {
        double x, y, z;
        x = Math.abs(this.x);
        y = Math.abs(this.y);
        z = Math.abs(this.z);
        if (x <= y && x <= z) {
            return new Vector(1, 0, 0);
        } else if (y <= x && y <= z) {
            return new Vector(0, 1, 0);
        }
        return new Vector(0, 0, 1);
    }

    public double MinComponent() {
        return Math.min(Math.min(this.x, this.y), this.z);
    }

    public double MaxComponent() {
        return Math.max(Math.max(this.x, this.y), this.z);
    }

    public Vector Reflect(Vector i) {
        return i.Sub(this.MulScalar(2 * this.Dot(i)));
    }

    public Vector Refract(Vector i, double n1, double n2) {
        double nr = n1 / n2;
        double cosI = -this.Dot(i);
        double sinT2 = nr * nr * (1 - cosI * cosI);
        if (sinT2 > 1) {
            return new Vector();
        }
        double cosT = Math.sqrt(1 - sinT2);
        return i.MulScalar(nr).Add(this.MulScalar(nr * cosI - cosT));
    }

    public double Reflectance(Vector i, double n1, double n2) {
        double nr = n1 / n2;
        double cosI = -this.Dot(i);
        double sinT2 = nr * nr * (1 - cosI * cosI);
        if (sinT2 > 1) {
            return 1;
        }
        double cosT = Math.sqrt(1 - sinT2);
        double rOrth = (n1 * cosI - n2 * cosT) / (n1 * cosI + n2 * cosT);
        double rPar = (n2 * cosI - n1 * cosT) / (n2 * cosI + n1 * cosT);
        return (rOrth * rOrth + rPar * rPar) / 2;
    }
}
