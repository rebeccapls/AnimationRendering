package jagex.graphic;

import java.awt.image.*;

public class DrawArea {

    public int[] pixels;
    public float[] luma;
    public int width;
    public int height;
    public BufferedImage image;

    public DrawArea(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
        this.luma = new float[width * height];
        bind();
    }

    public void bind() {
        Draw2D.prepare(pixels, width, height);
    }
}
