package gopt2j;

class SphericalHarmonic extends TransformedShape implements IShape {

    Material PositiveMaterial;
    Material NegativeMaterial;
    double harmonicFunction;
    Mesh mesh;
    
    @Override
    public Material MaterialAt(Vector p) {
        double h = this.EvaluateHarmonic(p);
        if (h < 0) {
            return this.NegativeMaterial;
        } else {
            return this.PositiveMaterial;
        }
    }

    @Override
    public void Compile() {
        this.mesh.Compile();
    }

    @Override
    public Box BoundingBox() {
        double r = 1;
        return new Box(new Vector(-r, -r, -r), new Vector(r, r, r));
    }

    @Override
    public Hit Intersect(Ray r) {
        Hit hit = this.mesh.Intersect(r);
        if (!hit.Ok()) {
            return Hit.NoHit;
        }
        return new Hit(this, hit.T, null);
    }

    @Override
    public Vector UV(Vector p) {
        double u = Math.atan2(p.z, p.x);
        double v = Math.atan2(p.y, new Vector(p.x, 0, p.z).Length());
        u = (1 - (u + Math.PI)) / (2 * Math.PI);
        v = (v + Math.PI / 2) / Math.PI;
        return new Vector(u, v, 0);
    }

    @Override
    public Vector NormalAt(Vector p) {
        double e = 0.0001;
        double x = p.x;
        double y = p.y;
        double z = p.z;
        var n = new Vector(
                this.Evaluate(new Vector(x - e, y, z)) - this.EvaluateHarmonic(new Vector(x + e, y, z)),
                this.Evaluate(new Vector(x, y - e, z)) - this.Evaluate(new Vector(x, y + e, z)),
                this.Evaluate(new Vector(x, y, z - e)) - this.Evaluate(new Vector(x, y, z + e))
        );

        return n.Normalize();
    }

    double Evaluate(Vector p) {
        return p.Length() - Math.abs(this.harmonicFunction(p.Normalize()));
    }

    double EvaluateHarmonic(Vector p) {
        return this.harmonicFunction(p.Normalize());
    }

    double harmonicFunction(Vector p) {

        return 0;
    }

    double sh00() {
        return 0.282095;
    }

    double sh1n1(Vector d) {
        return -0.488603 * d.y;
    }

    double sh10(Vector d) {
        return 0.488603 * d.z;
    }

    double sh1p1(Vector d) {
        return -0.488603 * d.x;
    }

    double sh2n2(Vector d) {
        // 0.5 * sqrt(15/pi) * x * y
        return 1.092548 * d.x * d.y;
    }

    double sh2n1(Vector d) {
        // -0.5 * sqrt(15/pi) * y * z
        return -1.092548 * d.y * d.z;
    }

    double sh20(Vector d) {
        // 0.25 * sqrt(5/pi) * (-x^2-y^2+2z^2)
        return 0.315392 * (-d.x * d.x - d.y * d.y + 2.0 * d.z * d.z);
    }

    double sh2p1(Vector d) {
        // -0.5 * sqrt(15/pi) * x * z
        return -1.092548 * d.x * d.z;
    }

    double sh2p2(Vector d) {
        // 0.25 * sqrt(15/pi) * (x^2 - y^2)
        return 0.546274 * (d.x * d.x - d.y * d.y);
    }

    double sh3n3(Vector d) {
        // -0.25 * sqrt(35/(2pi)) * y * (3x^2 - y^2)
        return -0.590044 * d.y * (3.0 * d.x * d.x - d.y * d.y);
    }

    double sh3n2(Vector d) {
        // 0.5 * sqrt(105/pi) * x * y * z
        return 2.890611 * d.x * d.y * d.z;
    }

    double sh3n1(Vector d) {
        // -0.25 * sqrt(21/(2pi)) * y * (4z^2-x^2-y^2)
        return -0.457046 * d.y * (4.0 * d.z * d.z - d.x * d.x - d.y * d.y);
    }

    double sh30(Vector d) {
        // 0.25 * sqrt(7/pi) * z * (2z^2 - 3x^2 - 3y^2)
        return 0.373176 * d.z * (2.0 * d.z * d.z - 3.0 * d.x * d.x - 3.0 * d.y * d.y);
    }

    double sh3p1(Vector d) {
        // -0.25 * sqrt(21/(2pi)) * x * (4z^2-x^2-y^2)
        return -0.457046 * d.x * (4.0 * d.z * d.z - d.x * d.x - d.y * d.y);
    }

    double sh3p2(Vector d) {
        // 0.25 * sqrt(105/pi) * z * (x^2 - y^2)
        return 1.445306 * d.z * (d.x * d.x - d.y * d.y);
    }

    double sh3p3(Vector d) {
        // -0.25 * sqrt(35/(2pi)) * x * (x^2-3y^2)
        return -0.590044 * d.x * (d.x * d.x - 3.0 * d.y * d.y);
    }

    double sh4n4(Vector d) {
        // 0.75 * sqrt(35/pi) * x * y * (x^2-y^2)
        return 2.503343 * d.x * d.y * (d.x * d.x - d.y * d.y);
    }

    double sh4n3(Vector d) {
        // -0.75 * sqrt(35/(2pi)) * y * z * (3x^2-y^2)
        return -1.770131 * d.y * d.z * (3.0 * d.x * d.x - d.y * d.y);
    }

    double sh4n2(Vector d) {
        // 0.75 * sqrt(5/pi) * x * y * (7z^2-1)
        return 0.946175 * d.x * d.y * (7.0 * d.z * d.z - 1.0);
    }

    double sh4n1(Vector d) {
        // -0.75 * sqrt(5/(2pi)) * y * z * (7z^2-3)
        return -0.669047 * d.y * d.z * (7.0 * d.z * d.z - 3.0);
    }

    double sh40(Vector d) {
        // 3/16 * sqrt(1/pi) * (35z^4-30z^2+3)
        double z2 = d.z * d.z;
        return 0.105786 * (35.0 * z2 * z2 - 30.0 * z2 + 3.0);
    }

    double sh4p1(Vector d) {
        // -0.75 * sqrt(5/(2pi)) * x * z * (7z^2-3)
        return -0.669047 * d.x * d.z * (7.0 * d.z * d.z - 3.0);
    }

    double sh4p2(Vector d) {
        // 3/8 * sqrt(5/pi) * (x^2 - y^2) * (7z^2 - 1)
        return 0.473087 * (d.x * d.x - d.y * d.y) * (7.0 * d.z * d.z - 1.0);
    }

    double sh4p3(Vector d) {
        // -0.75 * sqrt(35/(2pi)) * x * z * (x^2 - 3y^2)
        return -1.770131 * d.x * d.z * (d.x * d.x - 3.0 * d.y * d.y);
    }

    double sh4p4(Vector d) {
        // 3/16*sqrt(35/pi) * (x^2 * (x^2 - 3y^2) - y^2 * (3x^2 - y^2))
        double x2 = d.x * d.x;
        double y2 = d.y * d.y;
        return 0.625836 * (x2 * (x2 - 3.0 * y2) - y2 * (3.0 * x2 - y2));
    }

    double shFunc(int l, int m, Vector p) {
        double f = 0;

        if (l == 0 && m == 0) {
            f = sh00();
        } else if (l == 1 && m == -1) {
            f = sh1n1(p);
        } else if (l == 1 && m == 0) {
            f = sh10(p);
        } else if (l == 1 && m == 1) {
            f = sh1p1(p);
        } else if (l == 2 && m == -2) {
            f = sh2n2(p);
        } else if (l == 2 && m == -1) {
            f = sh2n1(p);
        } else if (l == 2 && m == 0) {
            f = sh20(p);
        } else if (l == 2 && m == 1) {
            f = sh2p1(p);
        } else if (l == 2 && m == 2) {
            f = sh2p2(p);
        } else if (l == 3 && m == -3) {
            f = sh3n3(p);
        } else if (l == 3 && m == -2) {
            f = sh3n2(p);
        } else if (l == 3 && m == -1) {
            f = sh3n1(p);
        } else if (l == 3 && m == 0) {
            f = sh30(p);
        } else if (l == 3 && m == 1) {
            f = sh3p1(p);
        } else if (l == 3 && m == 2) {
            f = sh3p2(p);
        } else if (l == 3 && m == 3) {
            f = sh3p3(p);
        } else if (l == 4 && m == -4) {
            f = sh4n4(p);
        } else if (l == 4 && m == -3) {
            f = sh4n3(p);
        } else if (l == 4 && m == -2) {
            f = sh4n2(p);
        } else if (l == 4 && m == -1) {
            f = sh4n1(p);
        } else if (l == 4 && m == 0) {
            f = sh40(p);
        } else if (l == 4 && m == 1) {
            f = sh4p1(p);
        } else if (l == 4 && m == 2) {
            f = sh4p2(p);
        } else if (l == 4 && m == 3) {
            f = sh4p3(p);
        } else if (l == 4 && m == 4) {
            f = sh4p4(p);
        } else {
            System.out.println("unsupported spherical harmonic");
        }
        return f;
    }
}
