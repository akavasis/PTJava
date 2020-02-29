package gopt2j;

import java.util.Random;
import gopt2j.Buffer.Channel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

final class Renderer {

    private Thread t;
    private String threadName;
    public Scene Scene;
    public Camera Camera;
    public Sampler Sampler;
    public Buffer PBuffer;
    public int SamplesPerPixel;
    public boolean StratifiedSampling;
    public int AdaptiveSamples;
    public double AdaptiveThreshold;
    public double AdaptiveExponent;
    public int FireflySamples;
    public double FireflyThreshold;
    public int NumCPU;
    public boolean Verbose;
    public int width;
    public int height;
    public int iterations;
    String pathTemplate;
    ThreadGroup tg;

    Renderer() {

    }

    Renderer(Scene scn, Camera cam, Sampler sampler, int width, int height) {

        System.out.println("Initialising core renderer...");

        this.Scene = scn;
        this.Camera = cam;
        this.Sampler = sampler;
        this.width = width;
        this.height = height;
        this.PBuffer = new Buffer(width, height);
        this.SamplesPerPixel = 1;
        this.StratifiedSampling = false;
        this.AdaptiveSamples = 0;
        this.AdaptiveThreshold = 1;
        this.AdaptiveExponent = 1;
        this.FireflySamples = 0;
        this.FireflyThreshold = 1;
        this.NumCPU = Runtime.getRuntime().availableProcessors();

    }

    static Renderer NewRenderer(Scene scene, Camera camera, Sampler sampler, int w, int h) {
        Renderer r = new Renderer();
        r.Scene = scene;
        r.Camera = camera;
        r.Sampler = sampler;
        r.PBuffer = new Buffer().NewBuffer(w, h);
        r.SamplesPerPixel = 1;
        r.StratifiedSampling = false;
        r.AdaptiveSamples = 0;
        r.AdaptiveThreshold = 1;
        r.AdaptiveExponent = 1;
        r.FireflySamples = 0;
        r.FireflyThreshold = 1;
        r.NumCPU = Runtime.getRuntime().availableProcessors();

        return r;
    }

    public void IterativeRender(String pathTemplate, int iterations) throws InterruptedException, IOException {
        this.iterations = iterations;
        
        for (int iter = 1; iter < this.iterations; iter++)
        {
            System.out.println("[Iteration:" + iter + " of " + iterations + "]");
            Buffer framebuffer = this.run();
            this.pathTemplate = pathTemplate;
            File outputfile = new File(pathTemplate);
            ImageIO.write(framebuffer.Image(framebuffer, Channel.ColorChannel), "png", outputfile);
            System.out.println("Iteration Completed. Writing image...");
        }
    }

    public Buffer run() throws IOException {

        Scene scene = this.Scene;
        Camera camera = this.Camera;
        Sampler sampler = this.Sampler;
        Buffer buf = this.PBuffer;
        int w = buf.W;
        int h = buf.H;
        int spp = this.SamplesPerPixel;
        int sppRoot = (int) (Math.sqrt((double) (this.SamplesPerPixel)));
        int ncpu = 1;
        scene.Compile();
        scene.rays = 0;
        Random rand = new Random();
        double fu, fv;

        for (int i = 0; i < ncpu; i++)
            {
                for (int y = i; y < h; y += ncpu)
                {
                    for (int x = 0; x < w; x++)
                    {
                        if (this.StratifiedSampling)
                        {
                            for (int u = 0; u < sppRoot; u++)
                            {
                                for (int v = 0; v < sppRoot; v++)
                                {
                                    fu = ((double)u + 0.5) / (double)sppRoot;
                                    fv = ((double)v + 0.5) / (double)sppRoot;
                                    Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                                    Colour sample = sampler.Sample(scene, ray, rand);
                                    buf.AddSample(x, y, sample);
                                }
                            }
                        }
                        else
                        {
                            // Random subsampling
                            for (int ii = 0; ii < spp; ii++)
                            {
                                fu = rand.nextDouble();
                                fv = rand.nextDouble();
                                Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                                Colour sample = sampler.Sample(scene, ray, rand);
                                buf.AddSample(x, y, sample);
                            }
                        }
                        // Adaptive Sampling
                        if (this.AdaptiveSamples > 0)
                        {

                            double v = buf.StandardDeviation(x, y).MaxComponent();
                            v = Util.Clamp(v / AdaptiveThreshold, 0, 1);
                            v = Math.pow(v, AdaptiveExponent);
                            int samples = (int)(v * AdaptiveSamples);
                            for (int d = 0; d < samples; d++)
                            {

                                fu = rand.nextDouble();
                                fv = rand.nextDouble();
                                Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                                Colour sample = sampler.Sample(scene, ray, rand);
                                buf.AddSample(x, y, sample);
                            }
                        }

                        if (this.FireflySamples > 0)
                        {
                            if (this.PBuffer.StandardDeviation(x, y).MaxComponent() > this.FireflyThreshold)
                            {
                                for (int e = 0; e < this.FireflySamples; e++)
                                {
                                    fu = rand.nextDouble();
                                    fv = rand.nextDouble();
                                    Ray ray = camera.CastRay(x, y, w, h, fu, fv, rand);
                                    Colour sample = sampler.Sample(scene, ray, rand);
                                    this.PBuffer.AddSample(x, y, sample);
                                }
                            }
                        }
                    }
                }
            }
        return buf;
    }

    void FrameRender(String path, int iterations) throws IOException {
        for (int i = 1; i <= iterations; i++) {
            System.out.println("Iterations " + i + "of " + iterations);
            run();
        }
        Buffer buf = PBuffer.Copy();
        //this.writeImage(path, buf, Channel.ColorChannel);
    }
}
