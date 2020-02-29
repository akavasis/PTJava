package gopt2j;

import java.util.Random;

public class Vector
    {
        public static Vector ORIGIN = new Vector(0, 0, 0);

        public double X, Y, Z;

        public Vector() {}

        public Vector(double x, double y, double z)
        {
            X = x;
            Y = y;
            Z = z;
        }
        
        public static Vector RandomUnitVector(Random rnd)
        {
            for (;;)
            {
                double x, y, z;

                if (rnd == null)
                {
                    x = new Random().nextDouble() * 2 - 1;
                    y = new Random().nextDouble() * 2 - 1;
                    z = new Random().nextDouble() * 2 - 1;
                }
                else
                {
                    x = rnd.nextDouble() * 2 - 1;
                    y = rnd.nextDouble() * 2 - 1;
                    z = rnd.nextDouble() * 2 - 1;
                }
                if (x * x + y * y + z * z > 1)
                {
                    continue;
                }
                return new Vector(x, y, z).Normalize();
            }
        }
        
        public double Length()
        {
            return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
        }
        
        public double LengthN(double n)
        {
            if (n == 2)
            {
                return this.Length();
            }
            Vector a = this.Abs();
            return Math.pow(Math.pow(a.X, n) + Math.pow(a.Y, n) + Math.pow(a.Z, n), 1 / n);
        }
        
        public double Dot(Vector b)
        {
            return this.X * b.X + this.Y * b.Y + this.Z * b.Z;
        }

        public Vector Cross(Vector b)
        {
            return new Vector(this.Y * b.Z - this.Z * b.Y,  
                              this.Z * b.X - this.X * b.Z, 
                              this.X * b.Y - this.Y * b.X);
        }

        public Vector Normalize()
        {
            double d = this.Length();
            return new Vector(this.X / d, this.Y / d, this.Z / d);
        }

        public Vector Negate()
        {
            return new Vector(-this.X, -this.Y, -this.Z);
        }

        Vector Abs()
        {
            return new Vector(Math.abs(this.X), Math.abs(this.Y), Math.abs(this.Z));
        }

        public Vector Add(Vector b)
        {
            return new Vector(this.X + b.X, this.Y + b.Y, this.Z + b.Z);
        }

        public Vector Sub(Vector b)
        {
            return new Vector(this.X - b.X, this.Y - b.Y, this.Z - b.Z);
        }

        public Vector Mul(Vector b)
        {
            return new Vector(this.X * b.X, this.Y * b.Y, this.Z * b.Z);
        }

        public Vector Div(Vector b)
        {
            return new Vector(this.X / b.X, this.Y / b.Y, this.Z / b.Z);
        }

        public Vector Mod(Vector b)
        {
            return new Vector(this.X - b.X * Math.floor(this.X / b.X), 
                              this.Y - b.Y * Math.floor(this.Y / b.Y), 
                              this.Z - b.Z * Math.floor(this.Z / b.Z));
        }
        
        public Vector AddScalar(double b)
        {
            return new Vector(this.X + b, this.Y + b, this.Z + b);
        }

        public Vector SubScalar(double b)
        {
            return new Vector(this.X - b, this.Y - b, this.Z - b);
        }

        public Vector MulScalar(double b)
        {
            return new Vector(this.X * b, this.Y * b, this.Z * b);
        }

        public Vector DivScalar(double b)
        {
            return new Vector(this.X / b, this.Y / b, this.Z / b);
        }

        public Vector Min(Vector b)
        {
            return new Vector(Math.min(this.X, b.X), Math.min(this.Y, b.Y), Math.min(this.Z, b.Z));
        }

        public Vector Max(Vector b)
        {
            return new Vector(Math.max(this.X, b.X), Math.max(this.Y, b.Y), Math.max(this.Z, b.Z));
        }

        public Vector MinAxis()
        {
            double x, y, z;
            x = Math.abs(this.X);
            y = Math.abs(this.Y);
            z = Math.abs(this.Z);

            if (x <= y && x <= z)
            {
                return new Vector(1, 0, 0);
            }
            else if (y <= x && y <= z)
            {
                return new Vector(0, 1, 0);
            }
            return new Vector(0, 0, 1);
        }

        public double MinComponent()
        {
            return Math.min(Math.min(this.X, this.Y), this.Z);
        }

        public double MaxComponent()
        {
            return Math.max(Math.max(this.X, this.Y), this.Z);
        }

        public Vector Reflect(Vector i)
        {
            return i.Sub(this.MulScalar(2 * this.Dot(i)));
        }

        public Vector Refract(Vector i, double n1, double n2)
        {
            double nr = n1 / n2;
            double cosI = -this.Dot(i);
            double sinT2 = nr * nr * (1 - cosI * cosI);
            if (sinT2 > 1)
            {
                return new Vector();
            }
            double cosT = Math.sqrt(1 - sinT2);
            return i.MulScalar(nr).Add(this.MulScalar(nr * cosI - cosT));
        }

        public double Reflectance(Vector i, double n1, double n2)
        {
            double nr = n1 / n2;
            double cosI = -this.Dot(i);
            double sinT2 = nr * nr * (1 - cosI * cosI);
            if (sinT2 > 1)
            {
                return 1;
            }
            double cosT = Math.sqrt(1 - sinT2);
            double rOrth = (n1 * cosI - n2 * cosT) / (n1 * cosI + n2 * cosT);
            double rPar = (n2 * cosI - n1 * cosT) / (n2 * cosI + n1 * cosT);
            return (rOrth * rOrth + rPar * rPar) / 2;
        }
    }