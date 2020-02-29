package gopt2j;

import java.io.IOException;

public class PTmain {

    public static void main(String[] args) throws InterruptedException, IOException {

        int numCPU = Runtime.getRuntime().availableProcessors();

        int width = 640;
        int height = 480;

        Scene scene = new Scene();

        // eye, center, up
        Camera camera = Camera.LookAt(new Vector(0, 50, 1), new Vector(4, 20, 5), new Vector(0, 0, 1), 50);

        // Light
        scene.Add(Sphere.NewSphere(new Vector(0, 0, 10), 0.5, Material.LightMaterial(new Colour(254, 254, 254), 5)));
        // Plane
        Plane plane = Plane.NewPlane(new Vector(0, 0, 0), new Vector(0, 0, 1), Material.SpecularMaterial(new Colour(1.0, 1.0, 1.0), 1));
        scene.Add(plane);

        // Sphere
        Material mat1 = Material.DiffuseMaterial(new Colour(1.0, 1.0, 1.0));
        Material mat2 = Material.GlossyMaterial(new Colour(1.0, 0.0, 1.0), 1.4, Util.Radians(30));
        Material mat3 = Material.MetallicMaterial(new Colour(1.0, 1.0, 0.0), 1.5, 1.0);
        Material mat4 = Material.SpecularMaterial(new Colour(0.0, 1.0, 1.0), 1.0);
        Material mat5 = Material.TransparentMaterial(new Colour(0.5, 0.5, 1.0), 2, Util.Radians(20), 1);
        Material mat6 = Material.ClearMaterial(0.5, 0.5);
        Material lightmaterial = Material.LightMaterial(Colour.White, 20);
        
        // Spheres
        scene.Add(Sphere.NewSphere(new Vector(-6, 0, 1), 1, mat1)); // Diffuse
        scene.Add(Sphere.NewSphere(new Vector(-4, 0, 1), 1, mat2)); // Glossy
        scene.Add(Sphere.NewSphere(new Vector(-2, 0, 1), 1, mat3)); // Metallic
        scene.Add(Sphere.NewSphere(new Vector(0, 0, 1), 1, mat4));  // Specular
        scene.Add(Sphere.NewSphere(new Vector(2, 0, 1), 1, mat5));  // Transparent
        scene.Add(Sphere.NewSphere(new Vector(4, 0, 1), 1, mat5));  // Clear
        scene.Add(Sphere.NewSphere(new Vector(6, 0, 1), 1, mat5));  // Clear

        Sampler sampler = new DefaultSampler().NewSampler(4, 4);
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, width, height);

        renderer.AdaptiveSamples = 128;
        renderer.StratifiedSampling = true;
        renderer.FireflySamples = 128;

        renderer.IterativeRender("out.png", 3);
    }
}
