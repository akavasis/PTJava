package gopt2j;

import java.io.IOException;

public class PTmain {

    public static void main(String[] args) throws InterruptedException, IOException {

        Scene scene = new Scene();
        double r = 0.4;
        Material material;
        material = Material.DiffuseMaterial(Colour.HexColor(0x334D5C));
        scene.Add(Sphere.NewSphere(new Vector(-2, r, 0), r, material));
        material = Material.SpecularMaterial(Colour.HexColor(0x334D5C), 2);
        scene.Add(Sphere.NewSphere(new Vector(-1, r, 0), r, material));
        material = Material.GlossyMaterial(Colour.HexColor(0x334D5C), 2, Util.Radians(50));
        scene.Add(Sphere.NewSphere(new Vector(0, r, 0), r, material));
        material = Material.TransparentMaterial(Colour.HexColor(0x334D5C), 2, Util.Radians(20), 1);
        scene.Add(Sphere.NewSphere(new Vector(1, r, 0), r, material));
        material = Material.ClearMaterial(2, 0);
        scene.Add(Sphere.NewSphere(new Vector(2, r, 0), r, material));
        material = Material.MetallicMaterial(Colour.HexColor(0xFFFFFF), 0, 1);
        scene.Add(Sphere.NewSphere(new Vector(0, 1.5, -4), 1.5, material));
        scene.Add(Cube.NewCube(new Vector(-1000, -1, -1000), new Vector(1000, 0, 1000), Material.GlossyMaterial(Colour.HexColor(0xFFFFFF), 1.4, Util.Radians(20))));
        scene.Add(Sphere.NewSphere(new Vector(0, 5, 0), 1, Material.LightMaterial(Colour.White, 25)));
        Camera camera = Camera.LookAt(new Vector(0, 3, 6), new Vector(0, 1, 0), new Vector(0, 1, 0), 30);
        DefaultSampler sampler = DefaultSampler.NewSampler(16, 16);
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, 1920, 1080);
        renderer.FireflySamples = 32;
        renderer.IterativeRender("materialspheres.png", 100);
    }
}
