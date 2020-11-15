package gopt2j;

import java.util.Random;
import gopt2j.Buffer.Channel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

final class Renderer implements Runnable {

    Scene Scene;
    Camera Camera;
    Sampler Sampler;
    Buffer PBuffer;
    int SamplesPerPixel;
    public boolean StratifiedSampling;
    public int AdaptiveSamples;
    double AdaptiveThreshold;
    double AdaptiveExponent;
    public int FireflySamples;
    double FireflyThreshold;
    
    private static Thread lookupThreads[];
    private int numThreads;
    
    int iterations;
    String pathTemplate;

    Renderer() {
        
    }

    public static Renderer NewRenderer(Scene scene, Camera camera, Sampler sampler, int w, int h) {
        Renderer r = new Renderer();
        r.Scene = scene;
        r.Camera = camera;
        r.Sampler = sampler;
        r.PBuffer = new Buffer(w, h);
        r.SamplesPerPixel = 1;
        r.StratifiedSampling = false;
        r.AdaptiveSamples = 0;
        r.AdaptiveExponent = 0;
        r.AdaptiveThreshold = 1;
        r.FireflySamples = 0;
        r.FireflyThreshold = 1;
        r.numThreads = Runtime.getRuntime().availableProcessors();
        lookupThreads = new Thread[r.numThreads];

        return r;
    }

    
    @Override
    public void run() {
        Scene scene = Scene;
        Camera camera = Camera;
        Sampler sampler = Sampler;
        Buffer buf = PBuffer;
        int w = buf.W;
        int h = buf.H;
        int spp = SamplesPerPixel;
        int sppRoot = (int) (Math.sqrt((double) (SamplesPerPixel)));
        scene.Compile();
        scene.rays = 0;
        Random rand = new Random();
        double fu, fv;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (StratifiedSampling) {
                    for (int u = 0; u < sppRoot; u++) {
                        for (int v = 0; v < sppRoot; v++) {
                            fu = ((double) u + 0.5) / (double) sppRoot;
                            fv = ((double) v + 0.5) / (double) sppRoot;
                            Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                            Colour sample = sampler.Sample(scene, ray, rand);
                            buf.AddSample(x, y, sample);
                        }
                    }
                } else {
                    // Random subsampling
                    for (int ii = 0; ii < spp; ii++) {
                        fu = rand.nextDouble();
                        fv = rand.nextDouble();
                        Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                        Colour sample = sampler.Sample(scene, ray, rand);
                        buf.AddSample(x, y, sample);
                    }
                }
                // Adaptive Sampling
                if (AdaptiveSamples > 0) {
                    double v = buf.StandardDeviation(x, y).MaxComponent();
                    v = Util.Clamp(v / AdaptiveThreshold, 0, 1);
                    v = Math.pow(v, AdaptiveExponent);
                    int samples = (int) (v * AdaptiveSamples);
                    for (int d = 0; d < samples; d++) {

                        fu = rand.nextDouble();
                        fv = rand.nextDouble();
                        Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                        Colour sample = sampler.Sample(scene, ray, rand);
                        buf.AddSample(x, y, sample);
                    }
                }

                if (FireflySamples > 0) {
                    if (PBuffer.StandardDeviation(x, y).MaxComponent() > FireflyThreshold) {
                        for (int e = 0; e < FireflySamples; e++) {
                            fu = rand.nextDouble();
                            fv = rand.nextDouble();
                            Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                            Colour sample = sampler.Sample(scene, ray, rand);
                            PBuffer.AddSample(x, y, sample);
                        }
                    }
                }
            }
        }
    }
    
    public void IterativeRender(String pathTemplate, int iterations) throws InterruptedException, IOException {
        this.iterations = iterations;

        for (int iter = 1; iter < this.iterations; iter++) {
            System.out.println("[Iteration:" + iter + " of " + iterations + "]");
            
            for (int i = 0; i < this.numThreads; i++) {
                lookupThreads[i] = new Thread(this);
                lookupThreads[i].start();
            }
            
            for (int i = 0; i < numThreads; i++) {
                try {
                    lookupThreads[i].join( );
                } catch (InterruptedException iex) {}
            }
            
            this.pathTemplate = pathTemplate;
            File outputfile = new File(pathTemplate);
            boolean write = ImageIO.write(PBuffer.Image(PBuffer, Channel.ColorChannel), "png", outputfile);
            System.out.println("Iteration Completed. Writing image...");
        }
    }
}
