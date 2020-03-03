/*
 * The MIT License
 *
 * Copyright 2020 akava.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package gopt2j;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author akava
 */
public class Example {

    Example() {
    }

    static void MaterialSpheres() throws IOException, InterruptedException {
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
        DefaultSampler sampler = new DefaultSampler().NewSampler(16, 16);
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, 1920, 1080);
        renderer.FireflySamples = 32;
        renderer.IterativeRender("materialspheres.png", 100);
    }

    static void Example1() throws IOException, InterruptedException {
        Scene scene = new Scene();
        scene.Add(Sphere.NewSphere(new Vector(1.5, 1.25, 0), 1.25, Material.SpecularMaterial(Colour.HexColor(0x004358), 1.3)));
        scene.Add(Sphere.NewSphere(new Vector(-1, 1, 2), 1, Material.SpecularMaterial(Colour.HexColor(0xFFE11A), 1.3)));
        scene.Add(Sphere.NewSphere(new Vector(-2.5, 0.75, 0), 0.75, Material.SpecularMaterial(Colour.HexColor(0xFD7400), 1.3)));
        scene.Add(Sphere.NewSphere(new Vector(-0.75, 0.5, -1), 0.5, Material.ClearMaterial(1.5, 0)));
        scene.Add(Cube.NewCube(new Vector(-10, -1, -10), new Vector(10, 0, 10), Material.GlossyMaterial(Colour.White, 1.1, Util.Radians(10))));
        scene.Add(Sphere.NewSphere(new Vector(-1.5, 4, 0), 0.5, Material.LightMaterial(Colour.White, 30)));
        Camera camera = Camera.LookAt(new Vector(0, 2, -5), new Vector(0, 0.25, 3), new Vector(0, 1, 0), 45);
        camera.SetFocus(new Vector(-0.75, 1, -1), 0.1);
        DefaultSampler sampler = new DefaultSampler().NewSampler(4, 8);
        sampler.SpecularMode = SpecularMode.SpecularModeFirst;
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, 1920 / 2, 1080 / 2);
        renderer.AdaptiveSamples = 32;
        renderer.FireflySamples = 256;
        renderer.IterativeRender("example1.png", 1000);
    }

    static void SimpleSphere() throws IOException, InterruptedException {
        Scene scene = new Scene();
        Material material = Material.DiffuseMaterial(Colour.White);
        Plane plane = Plane.NewPlane(new Vector(0, 0, 0), new Vector(0, 0, 1), material);
        scene.Add(plane);
        Material material2 = Material.GlossyMaterial(Colour.White, 1.1, Util.Radians(10));
        Sphere sphere = Sphere.NewSphere(new Vector(0, 0, 1), 1, material2);
        scene.Add(sphere);
        Sphere light = Sphere.NewSphere(new Vector(0, 0, 5), 1, Material.LightMaterial(Colour.White, 8));
        scene.Add(light);
        Camera camera = Camera.LookAt(new Vector(3, 3, 3), new Vector(0, 0, 0.5), new Vector(0, 0, 1), 50);
        DefaultSampler sampler = new DefaultSampler().NewSampler(4, 4);
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, 960, 540);
        renderer.AdaptiveSamples = 128;
        renderer.IterativeRender("simplesphere.png", 1000);
    }

    static void qbert() throws IOException, InterruptedException {
        Scene scene = new Scene();
        Material floor = Material.DiffuseMaterial(Colour.White);
        Material cube = Material.DiffuseMaterial(new Colour(0.3, 0.2, 0.6));
        Material ball = Material.DiffuseMaterial(new Colour(0.3, 0.7, 0.7));
        int n = 7;
        double fn = (double) n;

        for (int z = 0; z < n; z++) {
            for (int x = 0; x < n - z; x++) {
                for (int y = 0; y < n - z - x; y++) {
                    double fx = (double) x;
                    double fy = (double) y;
                    double fz = (double) z;
                    scene.Add(Cube.NewCube(new Vector(fx, fy, fz), new Vector(fx + 1, fy + 1, fz + 1), cube));

                    if (x + y == n - z - 1) {
                        if (new Random().nextDouble() > 0.75) {
                            scene.Add(Sphere.NewSphere(new Vector(fx + 0.5, fy + 0.5, fz + 1.5), 0.35, ball));
                        }
                    }
                }
            }
        }

        scene.Add(Cube.NewCube(new Vector(-1000, -1000, -1), new Vector(1000, 1000, 0), floor));
        scene.Add(Sphere.NewSphere(new Vector(fn, fn / 3, fn * 2), 1, Material.LightMaterial(Colour.White, 100)));
        Camera camera = Camera.LookAt(new Vector(fn * 2, fn * 2, fn * 2), new Vector(0, 0, fn / 4), new Vector(0, 0, 1), 35);
        DefaultSampler sampler = new DefaultSampler().NewSampler(4, 4);
        Renderer renderer = Renderer.NewRenderer(scene, camera, sampler, 1920, 1080);
        renderer.FireflySamples = 128;
        renderer.IterativeRender("qbert.png", 100);
    }

}
