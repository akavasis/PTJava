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
        
        
        for (IShape shape : Shapes) {
            
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

    /*RGBColor Color;
    Texture Texture;
    double TextureAngle;
    List<IShape> ShapeList;
    IShape[] Lights;
    IShape[] Shapes;
    Tree Tree;
    AtomicInteger rays; 
    int raycount;
    
    Scene(RGBColor color, Texture texture, double textureAngle, IShape[] shapes, IShape[] lights, Tree tree, AtomicInteger rays)
    {
        this.Color = color;
        this.Texture = texture;
        this.TextureAngle = textureAngle;
        this.Shapes = shapes;
        this.Lights = lights;
        this.Tree = tree;
        this.rays = rays;
    }

    Scene() {
        this.Color = new RGBColor();
        this.Shapes = new IShape[]{};
        this.Lights = new IShape[]{};
        ShapeList = new ArrayList<>();
        this.Texture = null;
        this.rays = new AtomicInteger(0);
    }

    void Compile() {
     
        for (IShape shape : this.Shapes)
        {
            shape.Compile();
        }
        if(Tree.)
        {
            this.Tree = new Tree().NewTree(Shapes);
        }
        
    }

    void Add(IShape shape) 
    {        
        this.Shapes = ArrayUtils.add(this.Shapes, shape);
        
        if(shape.MaterialAt(new Vector()).Emittance > 0)
        {
            this.Lights = ArrayUtils.add(this.Lights, shape);
        }
    }
  
    int RayCount() {
        return this.rays.get();
    }

    Hit Intersect(Ray r) {
        this.rays.getAndAdd(1);
        return this.Tree.Intersect(r);
    }*/
}
