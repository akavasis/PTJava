package gopt2j;

import java.util.ArrayList;
import java.util.List;

class Scene {

    Colour Color = new Colour();
    ITexture Texture;
    double TextureAngle = 0;
    List<IShape> shapeList = new ArrayList<>();
    List<IShape> lightList = new ArrayList<>();
    IShape[] Lights = new IShape[]{};
    IShape[] Shapes = new IShape[]{};
    Tree tree;
    int rays = 0;

    Scene() {
    }

    public void Compile() {
        for (IShape shape : Shapes) 
        {
            if(shape != null)
            {
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
