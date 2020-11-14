package gopt2j;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

class Scene {

    Colour Color; 
    ITexture Texture;
    double TextureAngle;
    List<IShape> shapeList; 
    List<IShape> lightList;
    IShape[] LightsArray;
    IShape[] ShapesArray;
    Tree tree;
    int rays;

    Scene() {
        Color = new Colour();
        TextureAngle = 0;
        shapeList = new ArrayList<>();
        lightList = new ArrayList<>();
        LightsArray = new IShape[]{};
        ShapesArray = new IShape[]{};
        rays = 0;
    }

    public void Compile() {
        for (IShape shape : ShapesArray) {
            if (shape != null) {
                shape.Compile();
            }
        }
        if (tree == null) {
            tree = new Tree(ShapesArray);
        }
    }

    void Add(IShape shape) {
        if (shape.MaterialAt(new Vector()).Emittance > 0) {
            lightList.add(shape);
            IShape[] light_list = new IShape[lightList.size()];
            LightsArray = lightList.toArray(light_list);
        }
        shapeList.add(shape);
        IShape[] shape_list = new IShape[shapeList.size()];
        ShapesArray = shapeList.toArray(shape_list);
    }

    int RayCount() {
        return rays;
    }

    Hit Intersect(Ray r) {
        rays++;
        return tree.Intersect(r);
    }
}
