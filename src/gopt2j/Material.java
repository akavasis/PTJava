package gopt2j;

public class Material {

    Colour Color;
    ITexture Texture;
    ITexture NormalTexture;
    ITexture BumpTexture;
    ITexture GlossTexture;
    double BumpMultiplier;
    double Emittance;
    double Index;
    double Gloss;
    double Tint;
    double Reflectivity;
    Boolean Transparent;

    Material() {
    }

    Material(Colour color, ITexture texture, ITexture normaltexture, ITexture bumptexture, ITexture glosstexture, double b, double e, double i, double g, double tint, double r, Boolean t) {
        Color = color;
        Texture = texture;
        NormalTexture = normaltexture;
        BumpTexture = bumptexture;
        GlossTexture = glosstexture;
        BumpMultiplier = b;
        Emittance = e;
        Index = i;
        Gloss = g;
        Tint = tint;
        Reflectivity = r;
        Transparent = t;
    }

    public static Material DiffuseMaterial(Colour color) {
        return new Material(color, null, null, null, null, 1, 0, 1, 0, 0, -1, false);
    }

    public static Material SpecularMaterial(Colour color, double index) {
        return new Material(color, null, null, null, null, 1, 0, index, 0, 0, -1, false);
    }

    public static Material GlossyMaterial(Colour color, double index, double gloss) {
        return new Material(color, null, null, null, null, 1, 0, index, gloss, 0, -1, false);
    }

    public static Material ClearMaterial(double index, double gloss) {
        return new Material(new Colour(0, 0, 0), null, null, null, null, 1, 0, index, gloss, 0, -1, true);
    }

    public static Material TransparentMaterial(Colour color, double index, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1, 0, index, gloss, tint, -1, true);
    }

    public static Material MetallicMaterial(Colour color, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1, 0, 1, gloss, tint, 1, false);
    }

    public static Material LightMaterial(Colour color, double emittance) {
        return new Material(color, null, null, null, null, 1, emittance, 1, 0, 0, -1, false);
    }

    static Material MaterialAt(IShape shape, Vector point) {
        Material material = shape.MaterialAt(point);
        Vector uv = shape.UV(point);

        if (material.Texture != null) {
            material.Color = material.Texture.Sample(uv.X, uv.Y);
        }

        if (material.GlossTexture != null) {
            var c = material.GlossTexture.Sample(uv.X, uv.Y);
            material.Gloss = (c.r + c.g + c.b) / 3;
        }

        return material;
    }
}
