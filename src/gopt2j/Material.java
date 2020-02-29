package gopt2j;

public class Material {

    Colour Color;
    Texture Texture;
    Texture NormalTexture;
    Texture BumpTexture;
    Texture GlossTexture;
    double BumpMultiplier;
    double Emittance;
    double Index;
    double Gloss;
    double Tint;
    double Reflectivity;
    Boolean Transparent;

    Material() {

    }

    Material(Colour color, Texture texture, Texture normaltexture, Texture bumptexture, Texture glosstexture, double b, double e, double i, double g, double tint, double r, Boolean t) {
        this.Color = color;
        this.Texture = texture;
        this.NormalTexture = normaltexture;
        this.BumpTexture = bumptexture;
        this.GlossTexture = glosstexture;
        this.BumpMultiplier = b;
        this.Emittance = e;
        this.Index = i;
        this.Gloss = g;
        this.Tint = tint;
        this.Reflectivity = r;
        this.Transparent = t;
    }

    static Material DiffuseMaterial(Colour color) {
        return new Material(color, null, null, null, null, 1, 0, 1, 0, 0, -1, false);
    }

    static Material SpecularMaterial(Colour color, double index) {
        return new Material(color, null, null, null, null, 1, 0, index, 0, 0, -1, false);
    }

    static Material GlossyMaterial(Colour color, double index, double gloss) {
        return new Material(color, null, null, null, null, 1, 0, index, gloss, 0, -1, false);
    }

    static Material ClearMaterial(double index, double gloss) {
        return new Material(new Colour(0, 0, 0), null, null, null, null, 1, 0, index, gloss, 0, -1, true);
    }

    static Material TransparentMaterial(Colour color, double index, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1, 0, index, gloss, tint, -1, true);
    }

    static Material MetallicMaterial(Colour color, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1, 0, 1, gloss, tint, 1, false);
    }

    static Material LightMaterial(Colour color, double emittance) {
        return new Material(color, null, null, null, null, 1, emittance, 1, 0, 0, -1, false);
    }

    static Material MaterialAt(IShape shape, Vector point) {
        Material material = shape.MaterialAt(point);
        Vector uv = shape.UV(point);
        if (material.Texture != null) {
            material.Color = material.Texture.Sample(uv.X, uv.Y);
        }
        if (material.GlossTexture != null) {
            Colour c = material.GlossTexture.Sample(uv.X, uv.Y);
            material.Gloss = (c.r + c.g + c.b) / 3;
        }
        return material;
    }
}
