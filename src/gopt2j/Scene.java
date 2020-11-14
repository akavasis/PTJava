package gopt2j;

import java.util.ArrayList;
import java.util.List;

class Scene {

    Colour Color; 
    ITexture Texture;
    double TextureAngle;
    List<IShape> shapeList; 
    List<IShape> lightList;
    IShape[] Lights;
    IShape[] Shapes;
    Tree tree;
    int rays;

    Scene() {
        Color = new Colour();
        TextureAngle = 0;
        shapeList = new ArrayList<>();
        lightList = new ArrayList<>();
        Lights = new IShape[0];
        Shapes = new IShape[0];
        rays = 0;
    }

    public void Compile() {
        for (IShape shape : Shapes) {
            if (shape != null) {
                shape.Compile();
            }
        }
        if (tree == null) {
            tree = new Tree(Shapes);
        }
    }

    void Add(IShape shape) {
        shapeList.add(shape);
        if (shape.MaterialAt(new Vector()).Emittance > 0) {
            lightList.add(shape);
            Lights = lightList.toArray(Shapes);
        }
        Shapes = shapeList.toArray(Shapes);
    }

    int RayCount() {
        return rays;
    }

    Hit Intersect(Ray r) {
        rays++;
        return tree.Intersect(r);
    }
}
