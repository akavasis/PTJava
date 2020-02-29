package gopt2j;

public class Matrix {

     double xd00, xd01, xd02, xd03, 
            xd10, xd11, xd12, xd13,
            xd20, xd21, xd22, xd23,
            xd30, xd31, xd32, xd33;
        
        public Matrix() { }
        
        public Matrix(double xd00, double xd01, double xd02, double xd03, double xd10, double xd11, double xd12, double xd13, double xd20, double xd21,
                double xd22, double xd23, double xd30, double xd31, double xd32, double xd33)
        {
            this.xd00 = xd00;
            this.xd01 = xd01;
            this.xd02 = xd02;
            this.xd03 = xd03;
            this.xd10 = xd10;
            this.xd11 = xd11;
            this.xd12 = xd12;
            this.xd13 = xd13;
            this.xd20 = xd20;
            this.xd21 = xd21;
            this.xd22 = xd22;
            this.xd23 = xd23;
            this.xd30 = xd30;
            this.xd31 = xd31;
            this.xd32 = xd32;
            this.xd33 = xd33;
        }

        public Matrix Identity()
        {
            return new Matrix(1, 0, 0, 0,
                              0, 1, 0, 0,
                              0, 0, 1, 0,
                              0, 0, 0, 1);
        }
       
        public Matrix Translate(Vector v)
        {
            Matrix tmatrix = new Matrix(1, 0, 0, v.X,
                                        0, 1, 0, v.Y,
                                        0, 0, 1, v.Z,
                                        0, 0, 0, 1);
            return tmatrix.Mul(this);
        }

        public static Matrix LookAtMatrix(Vector eye, Vector center, Vector up)
        {
            up = up.Normalize();
            Vector f = center.Sub(eye).Normalize();
            Vector s = f.Cross(up).Normalize();
            Vector u = s.Cross(f);

            Matrix m = new Matrix(s.X, u.X, f.X, 0,
                                  s.Y, u.Y, f.Y, 0,
                                  s.Z, u.Z, f.Z, 0,
                                    0, 0, 0, 1);

            return m.Transpose().Inverse().Translate(eye);
        }
        
        public Matrix Scale(Vector v)
        {
            Matrix scale = new Matrix(v.X, 0, 0, 0,
                                        0, v.Y, 0, 0,
                                        0, 0, v.Z, 0,
                                        0, 0, 0, 1);
            return scale.Mul(this);
        }
 
        public Matrix Rotate(Vector v, double a)
        {
            v = v.Normalize();
            double s = Math.sin(a);
            double c = Math.cos(a);
            double m = 1 - c;

            Matrix rMatrix = new Matrix(m * v.X * v.X + c      , m* v.X* v.Y + v.Z * s, m* v.Z* v.X - v.Y * s, 0,
		                                m * v.X * v.Y - v.Z * s, m* v.Y* v.Y + c      , m* v.Y* v.Z + v.X * s, 0,
		                                m * v.Z * v.X + v.Y * s, m* v.Y* v.Z - v.X * s, m* v.Z* v.Z + c      , 0,
		                                0, 0, 0, 1);
            return rMatrix.Mul(this);
        }

        public Matrix Frustum(double l, double r, double b, double t, double n, double f)
        {
            double t1 = 2 * n;
            double t2 = r - l;
            double t3 = t - b;
            double t4 = f - n;

            Matrix d = new Matrix(t1 / t2, 0, (r + l) / t2, 0,
                                  0, t1 / t3, (t + b) / t3, 0,
                                  0, 0, (-f - n) / t4, (-t1 * f) / t4,
                                  0, 0, -1, 0);

            return d.Mul(this);
        }

        public Matrix Orthographic(double l, double r, double b, double t, double n, double f)
        {
            Matrix d = new Matrix(2 / (r - l),           0,            0, -(r + l) / (r - l),
                                            0, 2 / (t - b),            0, -(t + b) / (t - b),
                                            0,           0, -2 / (f - n), -(f + n) / (f - n),
                                            0,           0,            0,                 1);

            return d.Mul(this);

        }

        public Matrix Perspective(double fovy, double aspect, double near, double far)
        {
            double ymax = near * Math.tan(fovy * Math.PI / 360);
            double xmax = ymax * aspect;
            return Frustum(-xmax, xmax, -ymax, ymax, near, far).Mul(this);
        }

        public Matrix Mul(Matrix b)
        {
            Matrix m = new Matrix();
            m.xd00 = this.xd00 * b.xd00 + this.xd01 * b.xd10 + this.xd02 * b.xd20 + this.xd03 * b.xd30;
            m.xd10 = this.xd10 * b.xd00 + this.xd11 * b.xd10 + this.xd12 * b.xd20 + this.xd13 * b.xd30;
            m.xd20 = this.xd20 * b.xd00 + this.xd21 * b.xd10 + this.xd22 * b.xd20 + this.xd23 * b.xd30;
            m.xd30 = this.xd30 * b.xd00 + this.xd31 * b.xd10 + this.xd32 * b.xd20 + this.xd33 * b.xd30;
            m.xd01 = this.xd00 * b.xd01 + this.xd01 * b.xd11 + this.xd02 * b.xd21 + this.xd03 * b.xd31;
            m.xd11 = this.xd10 * b.xd01 + this.xd11 * b.xd11 + this.xd12 * b.xd21 + this.xd13 * b.xd31;
            m.xd21 = this.xd20 * b.xd01 + this.xd21 * b.xd11 + this.xd22 * b.xd21 + this.xd23 * b.xd31;
            m.xd31 = this.xd30 * b.xd01 + this.xd31 * b.xd11 + this.xd32 * b.xd21 + this.xd33 * b.xd31;
            m.xd02 = this.xd00 * b.xd02 + this.xd01 * b.xd12 + this.xd02 * b.xd22 + this.xd03 * b.xd32;
            m.xd12 = this.xd10 * b.xd02 + this.xd11 * b.xd12 + this.xd12 * b.xd22 + this.xd13 * b.xd32;
            m.xd22 = this.xd20 * b.xd02 + this.xd21 * b.xd12 + this.xd22 * b.xd22 + this.xd23 * b.xd32;
            m.xd32 = this.xd30 * b.xd02 + this.xd31 * b.xd12 + this.xd32 * b.xd22 + this.xd33 * b.xd32;
            m.xd03 = this.xd00 * b.xd03 + this.xd01 * b.xd13 + this.xd02 * b.xd23 + this.xd03 * b.xd33;
            m.xd13 = this.xd10 * b.xd03 + this.xd11 * b.xd13 + this.xd12 * b.xd23 + this.xd13 * b.xd33;
            m.xd23 = this.xd20 * b.xd03 + this.xd21 * b.xd13 + this.xd22 * b.xd23 + this.xd23 * b.xd33;
            m.xd33 = this.xd30 * b.xd03 + this.xd31 * b.xd13 + this.xd32 * b.xd23 + this.xd33 * b.xd33;
            return m;
        }
       
        public Vector MulPosition(Vector b)
        {
            double xval = this.xd00 * b.X + this.xd01 * b.Y + this.xd02 * b.Z + this.xd03;
            double yval = this.xd10 * b.X + this.xd11 * b.Y + this.xd12 * b.Z + this.xd13;
            double zval = this.xd20 * b.X + this.xd21 * b.Y + this.xd22 * b.Z + this.xd23;

            return new Vector(xval, yval, zval);
        }

        public Vector MulDirection(Vector b)
        {
            double x = this.xd00 * b.X + this.xd01 * b.Y + this.xd02 * b.Z;
            double y = this.xd10 * b.X + this.xd11 * b.Y + this.xd12 * b.Z;
            double z = this.xd20 * b.X + this.xd21 * b.Y + this.xd22 * b.Z;
            return new Vector(x, y, z).Normalize();
        }

        public Ray MulRay(Ray b)
        {
            return new Ray(this.MulPosition(b.Origin), this.MulDirection(b.Direction));
        }

        public Box MulBox(Box box)
        {
            Vector r = new Vector(this.xd00, this.xd10, this.xd20);
            Vector u = new Vector(this.xd01, this.xd11, this.xd21);
            Vector b = new Vector(this.xd02, this.xd12, this.xd22);
            Vector t = new Vector(this.xd03, this.xd13, this.xd23);
            Vector xa = r.MulScalar(box.Min.X);
            Vector xb = r.MulScalar(box.Max.X);
            Vector ya = u.MulScalar(box.Min.Y);
            Vector yb = u.MulScalar(box.Max.Y);
            Vector za = b.MulScalar(box.Min.Z);
            Vector zb = b.MulScalar(box.Max.Z);
            Vector min = xa.Min(xb).Add(ya.Min(yb)).Add(za.Min(zb)).Add(t);
            Vector max = xa.Min(xb).Add(ya.Max(yb)).Add(za.Max(zb)).Add(t);
            return new Box(min, max);
        }

        public Matrix Transpose()
        {
            return new Matrix(this.xd00, this.xd10, this.xd20, this.xd30,
                              this.xd01, this.xd11, this.xd21, this.xd31,
                              this.xd02, this.xd12, this.xd22, this.xd32,
                              this.xd03, this.xd13, this.xd23, this.xd33);
        }
        
        public double Determinant()
        {
            return (this.xd00 * this.xd11 * this.xd22 * this.xd33 - this.xd00 * this.xd11 * this.xd23 * this.xd32 +
                    this.xd00 * this.xd12 * this.xd23 * this.xd31 - this.xd00 * this.xd12 * this.xd21 * this.xd33 +
                    this.xd00 * this.xd13 * this.xd21 * this.xd32 - this.xd00 * this.xd13 * this.xd22 * this.xd31 -
                    this.xd01 * this.xd12 * this.xd23 * this.xd30 + this.xd01 * this.xd12 * this.xd20 * this.xd33 -
                    this.xd01 * this.xd13 * this.xd20 * this.xd32 + this.xd01 * this.xd13 * this.xd22 * this.xd30 -
                    this.xd01 * this.xd10 * this.xd22 * this.xd33 + this.xd01 * this.xd10 * this.xd23 * this.xd32 +
                    this.xd02 * this.xd13 * this.xd20 * this.xd31 - this.xd02 * this.xd13 * this.xd21 * this.xd30 +
                    this.xd02 * this.xd10 * this.xd21 * this.xd33 - this.xd02 * this.xd10 * this.xd23 * this.xd31 +
                    this.xd02 * this.xd11 * this.xd23 * this.xd30 - this.xd02 * this.xd11 * this.xd20 * this.xd33 -
                    this.xd03 * this.xd10 * this.xd21 * this.xd32 + this.xd03 * this.xd10 * this.xd22 * this.xd31 -
                    this.xd03 * this.xd11 * this.xd22 * this.xd30 + this.xd03 * this.xd11 * this.xd20 * this.xd32 -
                    this.xd03 * this.xd12 * this.xd20 * this.xd31 + this.xd03 * this.xd12 * this.xd21 * this.xd30);
        }

        public Matrix Inverse()
        {
            Matrix m = new Matrix();
            double d = this.Determinant();
            m.xd00 = (this.xd12 * this.xd23 * this.xd31 - this.xd13 * this.xd22 * this.xd31 + this.xd13 * this.xd21 * this.xd32 - this.xd11 * this.xd23 * this.xd32 - this.xd12 * this.xd21 * this.xd33 + this.xd11 * this.xd22 * this.xd33) / d;
            m.xd01 = (this.xd03 * this.xd22 * this.xd31 - this.xd02 * this.xd23 * this.xd31 - this.xd03 * this.xd21 * this.xd32 + this.xd01 * this.xd23 * this.xd32 + this.xd02 * this.xd21 * this.xd33 - this.xd01 * this.xd22 * this.xd33) / d;
            m.xd02 = (this.xd02 * this.xd13 * this.xd31 - this.xd03 * this.xd12 * this.xd31 + this.xd03 * this.xd11 * this.xd32 - this.xd01 * this.xd13 * this.xd32 - this.xd02 * this.xd11 * this.xd33 + this.xd01 * this.xd12 * this.xd33) / d;
            m.xd03 = (this.xd03 * this.xd12 * this.xd21 - this.xd02 * this.xd13 * this.xd21 - this.xd03 * this.xd11 * this.xd22 + this.xd01 * this.xd13 * this.xd22 + this.xd02 * this.xd11 * this.xd23 - this.xd01 * this.xd12 * this.xd23) / d;
            m.xd10 = (this.xd13 * this.xd22 * this.xd30 - this.xd12 * this.xd23 * this.xd30 - this.xd13 * this.xd20 * this.xd32 + this.xd10 * this.xd23 * this.xd32 + this.xd12 * this.xd20 * this.xd33 - this.xd10 * this.xd22 * this.xd33) / d;
            m.xd11 = (this.xd02 * this.xd23 * this.xd30 - this.xd03 * this.xd22 * this.xd30 + this.xd03 * this.xd20 * this.xd32 - this.xd00 * this.xd23 * this.xd32 - this.xd02 * this.xd20 * this.xd33 + this.xd00 * this.xd22 * this.xd33) / d;
            m.xd12 = (this.xd03 * this.xd12 * this.xd30 - this.xd02 * this.xd13 * this.xd30 - this.xd03 * this.xd10 * this.xd32 + this.xd00 * this.xd13 * this.xd32 + this.xd02 * this.xd10 * this.xd33 - this.xd00 * this.xd12 * this.xd33) / d;
            m.xd13 = (this.xd02 * this.xd13 * this.xd20 - this.xd03 * this.xd12 * this.xd20 + this.xd03 * this.xd10 * this.xd22 - this.xd00 * this.xd13 * this.xd22 - this.xd02 * this.xd10 * this.xd23 + this.xd00 * this.xd12 * this.xd23) / d;
            m.xd20 = (this.xd11 * this.xd23 * this.xd30 - this.xd13 * this.xd21 * this.xd30 + this.xd13 * this.xd20 * this.xd31 - this.xd10 * this.xd23 * this.xd31 - this.xd11 * this.xd20 * this.xd33 + this.xd10 * this.xd21 * this.xd33) / d;
            m.xd21 = (this.xd03 * this.xd21 * this.xd30 - this.xd01 * this.xd23 * this.xd30 - this.xd03 * this.xd20 * this.xd31 + this.xd00 * this.xd23 * this.xd31 + this.xd01 * this.xd20 * this.xd33 - this.xd00 * this.xd21 * this.xd33) / d;
            m.xd22 = (this.xd01 * this.xd13 * this.xd30 - this.xd03 * this.xd11 * this.xd30 + this.xd03 * this.xd10 * this.xd31 - this.xd00 * this.xd13 * this.xd31 - this.xd01 * this.xd10 * this.xd33 + this.xd00 * this.xd11 * this.xd33) / d;
            m.xd23 = (this.xd03 * this.xd11 * this.xd20 - this.xd01 * this.xd13 * this.xd20 - this.xd03 * this.xd10 * this.xd21 + this.xd00 * this.xd13 * this.xd21 + this.xd01 * this.xd10 * this.xd23 - this.xd00 * this.xd11 * this.xd23) / d;
            m.xd30 = (this.xd12 * this.xd21 * this.xd30 - this.xd11 * this.xd22 * this.xd30 - this.xd12 * this.xd20 * this.xd31 + this.xd10 * this.xd22 * this.xd31 + this.xd11 * this.xd20 * this.xd32 - this.xd10 * this.xd21 * this.xd32) / d;
            m.xd31 = (this.xd01 * this.xd22 * this.xd30 - this.xd02 * this.xd21 * this.xd30 + this.xd02 * this.xd20 * this.xd31 - this.xd00 * this.xd22 * this.xd31 - this.xd01 * this.xd20 * this.xd32 + this.xd00 * this.xd21 * this.xd32) / d;
            m.xd32 = (this.xd02 * this.xd11 * this.xd30 - this.xd01 * this.xd12 * this.xd30 - this.xd02 * this.xd10 * this.xd31 + this.xd00 * this.xd12 * this.xd31 + this.xd01 * this.xd10 * this.xd32 - this.xd00 * this.xd11 * this.xd32) / d;
            m.xd33 = (this.xd01 * this.xd12 * this.xd20 - this.xd02 * this.xd11 * this.xd20 + this.xd02 * this.xd10 * this.xd21 - this.xd00 * this.xd12 * this.xd21 - this.xd01 * this.xd10 * this.xd22 + this.xd00 * this.xd11 * this.xd22) / d;
            return m;
        }
}
    
