package gopt2j;

import java.io.IOException;

public class PTmain {

    public static void main(String[] args) throws InterruptedException, IOException {

        int width = 1920;
            int height = 1080;
            Scene scene = new Scene();
            scene.Add(Sphere.NewSphere(new Vector(1.5, 1.25, 0), 1.25, Material.SpecularMaterial(Colour.HexColor(0x004358), 1.3)));
            scene.Add(Sphere.NewSphere(new Vector(-1, 1, 2), 1, Material.SpecularMaterial(Colour.HexColor(0xFFE11A), 1.3)));
            scene.Add(Sphere.NewSphere(new Vector(-2.5, 0.75, 0), 0.75, Material.SpecularMaterial(Colour.HexColor(0xFD7400), 1.3)));
            scene.Add(Sphere.NewSphere(new Vector(-0.75, 0.5, -1), 0.5, Material.ClearMaterial(1.5, 0)));
            scene.Add(Cube.NewCube(new Vector(-10, -1, -10), new Vector(10, 0, 10), Material.GlossyMaterial(Colour.White, 1.1, Util.Radians(10))));
            scene.Add(Sphere.NewSphere(new Vector(-1.5, 4, 0), 0.5, Material.LightMaterial(Colour.White, 30)));
            Camera camera = Camera.LookAt(new Vector(0, 2, -5), new Vector(0, 0.25, 3), new Vector(0, 1, 0), 45);
            camera.SetFocus(new Vector(-0.75, 1, -1), 0.1);
            DefaultSampler sampler = DefaultSampler.NewSampler(4, 8);
            sampler.SMode = SpecularMode.SpecularModeFirst;
            Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, width, height);
            //renderer.FireflySamples = 64;
            renderer.IterativeRender("sphere.png", 2);
    }
}
