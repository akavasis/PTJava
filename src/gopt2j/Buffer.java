package gopt2j;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class Buffer {

    public int W, H;
    public Pixel[] Pixels;
    public List<Pixel> PixelList = new ArrayList<>();
    byte[] imageBuffer;

    public enum Channel {
        ColorChannel, 
        VarianceChannel, 
        StandardDeviationChannel, 
        SamplesChannel
    }

    Buffer() {
    }

    Buffer(int width, int height) {
        this.W = width;
        this.H = height;
        imageBuffer = new byte[256 * 4 * height];
        this.Pixels = new Pixel[width * height];
        PixelList = new ArrayList<>(width * height);

        for (int i = 0; i < Pixels.length; i++) {
            Pixels[i] = new Pixel(0, new Colour(0, 0, 0), new Colour(0, 0, 0));
        }
    }

    Buffer(int width, int height, Pixel[] pbuffer) {
        this.W = width;
        this.H = height;
        this.Pixels = pbuffer;
    }

    Buffer NewBuffer(int w, int h) {
        Pixel[] pixbuffer = new Pixel[w * h];

        for (int i = 0; i < pixbuffer.length; i++) {
            pixbuffer[i] = new Pixel(0, new Colour(0, 0, 0), new Colour(0, 0, 0));
        }
        return new Buffer(w, h, pixbuffer);
    }

    Buffer Copy() {
        Pixel[] pixcopy = new Pixel[this.W * this.H];
        System.arraycopy(this.Pixels, 0, pixcopy, 0, this.Pixels.length);
        return new Buffer(this.W, this.H, pixcopy);
    }

    void AddSample(int x, int y, Colour sample) {
        Pixels[y * W + x].AddSample(sample);
    }

    int Samples(int x, int y) {
        return Pixels[y * W + x].Samples;
    }

    Colour Color(int x, int y) {
        return Pixels[y * W + x].Color();
    }

    Colour Variance(int x, int y) {
        return Pixels[y * W + x].Variance();
    }

    Colour StandardDeviation(int x, int y) {
        return Pixels[y * W + x].StandardDeviation();
    }

    BufferedImage Image(Buffer buf, Channel channel) {

        BufferedImage renderedImage = new BufferedImage(this.W, this.H, BufferedImage.TYPE_INT_RGB);
        
        double maxSamples = 0;
        if (channel == Channel.SamplesChannel) {
            for (Pixel pix : Pixels) {
                maxSamples = Math.max(maxSamples, (double) pix.Samples);
            }
        }

        Colour pixelColor = new Colour();

        for (int y = 0; y < this.H; y++) {
            for (int x = 0; x < this.W; x++) {
                switch (channel) {
                    case ColorChannel:
                        pixelColor = Pixels[y * W + x].Color().Pow(1 / 2.2);
                        break;
                    case VarianceChannel:
                        pixelColor = Pixels[y * W + x].Variance();
                        break;
                    case StandardDeviationChannel:
                        pixelColor = Pixels[y * W + x].StandardDeviation();
                        break;
                    case SamplesChannel:
                        double p = Pixels[y * W + x].Samples / maxSamples;
                        pixelColor = new Colour(p, p, p);
                        break;
                }
                int r = Math.max(0, Math.min(255, (int)(Math.floor(pixelColor.r * 256.0))));
                int g = Math.max(0, Math.min(255, (int)(Math.floor(pixelColor.g * 256.0))));
                int b = Math.max(0, Math.min(255, (int)(Math.floor(pixelColor.b * 256.0))));
                renderedImage.setRGB(x, y, 255 << 24 | (r<<16) | (g<<8) | b );
            }
        }
        return renderedImage;
    }

    class Pixel {

        public int Samples;
        public Colour M;
        public Colour V;

        public Pixel() { }

        public Pixel(int Samples, Colour M, Colour V) {
            this.Samples = Samples;
            this.M = M;
            this.V = V;
        }

        public void AddSample(Colour sample) {
            Samples++;

            if (Samples == 1) {
                M = sample;
                return;
            }

            Colour m = M;
            M = M.Add(sample.Sub(M).DivScalar((double)Samples));
            V = V.Add(sample.Sub(m).Mul(sample.Sub(M)));
        }

        public Colour Color() {
            return M;
        }

        public Colour Variance() {
            if (Samples < 2) {
                return new Colour(0, 0, 0);
            }
            return V.DivScalar((double)(Samples - 1));
        }

        public Colour StandardDeviation() {
            return Variance().Pow(0.5);
        }
    }
}