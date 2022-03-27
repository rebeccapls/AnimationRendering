package jagex.graphic;

public class Draw3D extends Draw2D {

    public static boolean testX;
    public static boolean jagged = true;
    public static int alpha;
    public static int centerX;
    public static int centerY;
    public static int[] reciprical15;
    public static int[] reciprical16;
    public static int[] sin;
    public static int[] cos;
    public static int[] offsets;
    public static int[] palette = new int[0x10000];

    static {
        reciprical15 = new int[512];
        reciprical16 = new int[2048];

        sin = new int[2048];
        cos = new int[2048];

        for (int i = 1; i < 512; i++) {
            reciprical15[i] = 0x8000 / i;
        }

        for (int j = 1; j < 2048; j++) {
            reciprical16[j] = 0x10000 / j;
        }

        for (int k = 0; k < 2048; k++) {
            sin[k] = (int) (65536D * Math.sin((double) k * 0.0030679614999999999D));
            cos[k] = (int) (65536D * Math.cos((double) k * 0.0030679614999999999D));
        }
    }

    /**
     * Adjusts the input RGB's brightness.
     *
     * @param rgb the color. (INT24_RGB)
     * @param exponent the exponent.
     * @return rgb^exponent.
     */
    public static int setBrightness(int rgb, double exponent) {
        double r = (double) (rgb >> 16) / 256.0;
        double g = (double) (rgb >> 8 & 0xff) / 256.0;
        double b = (double) (rgb & 0xff) / 256.0;
        r = Math.pow(r, exponent);
        g = Math.pow(g, exponent);
        b = Math.pow(b, exponent);
        return ((int) (r * 256.0) << 16) + ((int) (g * 256.0) << 8) + (int) (b * 256.0);
    }

    /**
     * Generates an HSL to RGB lookup table, also known as <i>palette</i>.
     *
     * @param exponent the brightness on a 0.0 to 1.0 scale.
     */
    public static final void createPalette(double exponent) {
        int off = 0;

        for (int y = 0; y < 512; y++) {
            double hue = (double) (y / 8) / 64.0 + 0.0078125;
            double saturation = (double) (y & 0x7) / 8.0 + 0.0625;

            for (int x = 0; x < 128; x++) {
                double lightness = (double) x / 128.0;
                double r = lightness;
                double g = lightness;
                double b = lightness;

                if (saturation != 0.0) {
                    double d_36_;

                    if (lightness < 0.5) {
                        d_36_ = lightness * (1.0 + saturation);
                    } else {
                        d_36_ = lightness + saturation - lightness * saturation;
                    }

                    double d_37_ = 2.0 * lightness - d_36_;
                    double d_38_ = hue + 0.3333333333333333;

                    if (d_38_ > 1.0) {
                        d_38_--;
                    }

                    double d_39_ = hue;
                    double d_40_ = hue - 0.3333333333333333;

                    if (d_40_ < 0.0) {
                        d_40_++;
                    }

                    if (6.0 * d_38_ < 1.0) {
                        r = d_37_ + (d_36_ - d_37_) * 6.0 * d_38_;
                    } else if (2.0 * d_38_ < 1.0) {
                        r = d_36_;
                    } else if (3.0 * d_38_ < 2.0) {
                        r = d_37_ + (d_36_ - d_37_) * (0.6666666666666666 - d_38_) * 6.0;
                    } else {
                        r = d_37_;
                    }

                    if (6.0 * d_39_ < 1.0) {
                        g = d_37_ + (d_36_ - d_37_) * 6.0 * d_39_;
                    } else if (2.0 * d_39_ < 1.0) {
                        g = d_36_;
                    } else if (3.0 * d_39_ < 2.0) {
                        g = d_37_ + (d_36_ - d_37_) * (0.6666666666666666 - d_39_) * 6.0;
                    } else {
                        g = d_37_;
                    }

                    if (6.0 * d_40_ < 1.0) {
                        b = d_37_ + (d_36_ - d_37_) * 6.0 * d_40_;
                    } else if (2.0 * d_40_ < 1.0) {
                        b = d_36_;
                    } else if (3.0 * d_40_ < 2.0) {
                        b = d_37_ + (d_36_ - d_37_) * (0.6666666666666666 - d_40_) * 6.0;
                    } else {
                        b = d_37_;
                    }
                }

                int rgb = ((int) (r * 256.0) << 16) + ((int) (g * 256.0) << 8) + (int) (b * 256.0);
                rgb = setBrightness(rgb, exponent);
                palette[off++] = rgb;
            }
        }
    }

    public static void prepareOffsets() {
        offsets = new int[height];
        for (int n = 0; n < height; n++) {
            offsets[n] = width * n;
        }

        centerX = width / 2;
        centerY = height / 2;
    }

    public static int powRGB(int rgb, double brightness) {
        double r = (double) (rgb >> 16) / 256D;
        double g = (double) (rgb >> 8 & 0xff) / 256D;
        double b = (double) (rgb & 0xff) / 256D;

        r = Math.pow(r, brightness);
        g = Math.pow(g, brightness);
        b = Math.pow(b, brightness);

        int intR = (int) (r * 256D);
        int intG = (int) (g * 256D);
        int intB = (int) (b * 256D);

        return (intR << 16) + (intG << 8) + intB;
    }

    public static void fillGouraudTriangle(int yA, int yB, int yC, int xA, int xB, int xC, int colorA, int colorB,
                                           int colorC) {
        int xStepAB = 0;
        int colorStepAB = 0;
        if (yB != yA) {
            xStepAB = (xB - xA << 16) / (yB - yA);
            colorStepAB = (colorB - colorA << 15) / (yB - yA);
        }

        int xStepBC = 0;
        int colorStepBC = 0;
        if (yC != yB) {
            xStepBC = (xC - xB << 16) / (yC - yB);
            colorStepBC = (colorC - colorB << 15) / (yC - yB);
        }

        int xStepAC = 0;
        int colorStepAC = 0;
        if (yC != yA) {
            xStepAC = (xA - xC << 16) / (yA - yC);
            colorStepAC = (colorA - colorC << 15) / (yA - yC);
        }

        if (yA <= yB && yA <= yC) {
            if (yA >= bottom)
                return;
            if (yB > bottom)
                yB = bottom;
            if (yC > bottom)
                yC = bottom;
            if (yB < yC) {
                xC = xA <<= 16;
                colorC = colorA <<= 15;
                if (yA < 0) {
                    xC -= xStepAC * yA;
                    xA -= xStepAB * yA;
                    colorC -= colorStepAC * yA;
                    colorA -= colorStepAB * yA;
                    yA = 0;
                }
                xB <<= 16;
                colorB <<= 15;
                if (yB < 0) {
                    xB -= xStepBC * yB;
                    colorB -= colorStepBC * yB;
                    yB = 0;
                }
                if (yA != yB && xStepAC < xStepAB || yA == yB && xStepAC > xStepBC) {
                    yC -= yB;
                    yB -= yA;
                    for (yA = offsets[yA]; --yB >= 0; yA += width) {
                        drawGouraudScanline(dest, yA, xC >> 16, xA >> 16, colorC >> 7, colorA >> 7);
                        xC += xStepAC;
                        xA += xStepAB;
                        colorC += colorStepAC;
                        colorA += colorStepAB;
                    }

                    while (--yC >= 0) {
                        drawGouraudScanline(dest, yA, xC >> 16, xB >> 16, colorC >> 7, colorB >> 7);
                        xC += xStepAC;
                        xB += xStepBC;
                        colorC += colorStepAC;
                        colorB += colorStepBC;
                        yA += width;
                    }
                    return;
                }
                yC -= yB;
                yB -= yA;
                for (yA = offsets[yA]; --yB >= 0; yA += width) {
                    drawGouraudScanline(dest, yA, xA >> 16, xC >> 16, colorA >> 7, colorC >> 7);
                    xC += xStepAC;
                    xA += xStepAB;
                    colorC += colorStepAC;
                    colorA += colorStepAB;
                }

                while (--yC >= 0) {
                    drawGouraudScanline(dest, yA, xB >> 16, xC >> 16, colorB >> 7, colorC >> 7);
                    xC += xStepAC;
                    xB += xStepBC;
                    colorC += colorStepAC;
                    colorB += colorStepBC;
                    yA += width;
                }
                return;
            }
            xB = xA <<= 16;
            colorB = colorA <<= 15;
            if (yA < 0) {
                xB -= xStepAC * yA;
                xA -= xStepAB * yA;
                colorB -= colorStepAC * yA;
                colorA -= colorStepAB * yA;
                yA = 0;
            }
            xC <<= 16;
            colorC <<= 15;
            if (yC < 0) {
                xC -= xStepBC * yC;
                colorC -= colorStepBC * yC;
                yC = 0;
            }
            if (yA != yC && xStepAC < xStepAB || yA == yC && xStepBC > xStepAB) {
                yB -= yC;
                yC -= yA;
                for (yA = offsets[yA]; --yC >= 0; yA += width) {
                    drawGouraudScanline(dest, yA, xB >> 16, xA >> 16, colorB >> 7, colorA >> 7);
                    xB += xStepAC;
                    xA += xStepAB;
                    colorB += colorStepAC;
                    colorA += colorStepAB;
                }

                while (--yB >= 0) {
                    drawGouraudScanline(dest, yA, xC >> 16, xA >> 16, colorC >> 7, colorA >> 7);
                    xC += xStepBC;
                    xA += xStepAB;
                    colorC += colorStepBC;
                    colorA += colorStepAB;
                    yA += width;
                }
                return;
            }
            yB -= yC;
            yC -= yA;
            for (yA = offsets[yA]; --yC >= 0; yA += width) {
                drawGouraudScanline(dest, yA, xA >> 16, xB >> 16, colorA >> 7, colorB >> 7);
                xB += xStepAC;
                xA += xStepAB;
                colorB += colorStepAC;
                colorA += colorStepAB;
            }

            while (--yB >= 0) {
                drawGouraudScanline(dest, yA, xA >> 16, xC >> 16, colorA >> 7, colorC >> 7);
                xC += xStepBC;
                xA += xStepAB;
                colorC += colorStepBC;
                colorA += colorStepAB;
                yA += width;
            }
            return;
        }
        if (yB <= yC) {
            if (yB >= bottom)
                return;
            if (yC > bottom)
                yC = bottom;
            if (yA > bottom)
                yA = bottom;
            if (yC < yA) {
                xA = xB <<= 16;
                colorA = colorB <<= 15;
                if (yB < 0) {
                    xA -= xStepAB * yB;
                    xB -= xStepBC * yB;
                    colorA -= colorStepAB * yB;
                    colorB -= colorStepBC * yB;
                    yB = 0;
                }
                xC <<= 16;
                colorC <<= 15;
                if (yC < 0) {
                    xC -= xStepAC * yC;
                    colorC -= colorStepAC * yC;
                    yC = 0;
                }
                if (yB != yC && xStepAB < xStepBC || yB == yC && xStepAB > xStepAC) {
                    yA -= yC;
                    yC -= yB;
                    for (yB = offsets[yB]; --yC >= 0; yB += width) {
                        drawGouraudScanline(dest, yB, xA >> 16, xB >> 16, colorA >> 7, colorB >> 7);
                        xA += xStepAB;
                        xB += xStepBC;
                        colorA += colorStepAB;
                        colorB += colorStepBC;
                    }

                    while (--yA >= 0) {
                        drawGouraudScanline(dest, yB, xA >> 16, xC >> 16, colorA >> 7, colorC >> 7);
                        xA += xStepAB;
                        xC += xStepAC;
                        colorA += colorStepAB;
                        colorC += colorStepAC;
                        yB += width;
                    }
                    return;
                }
                yA -= yC;
                yC -= yB;
                for (yB = offsets[yB]; --yC >= 0; yB += width) {
                    drawGouraudScanline(dest, yB, xB >> 16, xA >> 16, colorB >> 7, colorA >> 7);
                    xA += xStepAB;
                    xB += xStepBC;
                    colorA += colorStepAB;
                    colorB += colorStepBC;
                }

                while (--yA >= 0) {
                    drawGouraudScanline(dest, yB, xC >> 16, xA >> 16, colorC >> 7, colorA >> 7);
                    xA += xStepAB;
                    xC += xStepAC;
                    colorA += colorStepAB;
                    colorC += colorStepAC;
                    yB += width;
                }
                return;
            }
            xC = xB <<= 16;
            colorC = colorB <<= 15;
            if (yB < 0) {
                xC -= xStepAB * yB;
                xB -= xStepBC * yB;
                colorC -= colorStepAB * yB;
                colorB -= colorStepBC * yB;
                yB = 0;
            }
            xA <<= 16;
            colorA <<= 15;
            if (yA < 0) {
                xA -= xStepAC * yA;
                colorA -= colorStepAC * yA;
                yA = 0;
            }
            if (xStepAB < xStepBC) {
                yC -= yA;
                yA -= yB;
                for (yB = offsets[yB]; --yA >= 0; yB += width) {
                    drawGouraudScanline(dest, yB, xC >> 16, xB >> 16, colorC >> 7, colorB >> 7);
                    xC += xStepAB;
                    xB += xStepBC;
                    colorC += colorStepAB;
                    colorB += colorStepBC;
                }

                while (--yC >= 0) {
                    drawGouraudScanline(dest, yB, xA >> 16, xB >> 16, colorA >> 7, colorB >> 7);
                    xA += xStepAC;
                    xB += xStepBC;
                    colorA += colorStepAC;
                    colorB += colorStepBC;
                    yB += width;
                }
                return;
            }
            yC -= yA;
            yA -= yB;
            for (yB = offsets[yB]; --yA >= 0; yB += width) {
                drawGouraudScanline(dest, yB, xB >> 16, xC >> 16, colorB >> 7, colorC >> 7);
                xC += xStepAB;
                xB += xStepBC;
                colorC += colorStepAB;
                colorB += colorStepBC;
            }

            while (--yC >= 0) {
                drawGouraudScanline(dest, yB, xB >> 16, xA >> 16, colorB >> 7, colorA >> 7);
                xA += xStepAC;
                xB += xStepBC;
                colorA += colorStepAC;
                colorB += colorStepBC;
                yB += width;
            }
            return;
        }
        if (yC >= bottom)
            return;
        if (yA > bottom)
            yA = bottom;
        if (yB > bottom)
            yB = bottom;
        if (yA < yB) {
            xB = xC <<= 16;
            colorB = colorC <<= 15;
            if (yC < 0) {
                xB -= xStepBC * yC;
                xC -= xStepAC * yC;
                colorB -= colorStepBC * yC;
                colorC -= colorStepAC * yC;
                yC = 0;
            }
            xA <<= 16;
            colorA <<= 15;
            if (yA < 0) {
                xA -= xStepAB * yA;
                colorA -= colorStepAB * yA;
                yA = 0;
            }
            if (xStepBC < xStepAC) {
                yB -= yA;
                yA -= yC;
                for (yC = offsets[yC]; --yA >= 0; yC += width) {
                    drawGouraudScanline(dest, yC, xB >> 16, xC >> 16, colorB >> 7, colorC >> 7);
                    xB += xStepBC;
                    xC += xStepAC;
                    colorB += colorStepBC;
                    colorC += colorStepAC;
                }

                while (--yB >= 0) {
                    drawGouraudScanline(dest, yC, xB >> 16, xA >> 16, colorB >> 7, colorA >> 7);
                    xB += xStepBC;
                    xA += xStepAB;
                    colorB += colorStepBC;
                    colorA += colorStepAB;
                    yC += width;
                }
                return;
            }
            yB -= yA;
            yA -= yC;
            for (yC = offsets[yC]; --yA >= 0; yC += width) {
                drawGouraudScanline(dest, yC, xC >> 16, xB >> 16, colorC >> 7, colorB >> 7);
                xB += xStepBC;
                xC += xStepAC;
                colorB += colorStepBC;
                colorC += colorStepAC;
            }

            while (--yB >= 0) {
                drawGouraudScanline(dest, yC, xA >> 16, xB >> 16, colorA >> 7, colorB >> 7);
                xB += xStepBC;
                xA += xStepAB;
                colorB += colorStepBC;
                colorA += colorStepAB;
                yC += width;
            }
            return;
        }
        xA = xC <<= 16;
        colorA = colorC <<= 15;
        if (yC < 0) {
            xA -= xStepBC * yC;
            xC -= xStepAC * yC;
            colorA -= colorStepBC * yC;
            colorC -= colorStepAC * yC;
            yC = 0;
        }
        xB <<= 16;
        colorB <<= 15;
        if (yB < 0) {
            xB -= xStepAB * yB;
            colorB -= colorStepAB * yB;
            yB = 0;
        }
        if (xStepBC < xStepAC) {
            yA -= yB;
            yB -= yC;
            for (yC = offsets[yC]; --yB >= 0; yC += width) {
                drawGouraudScanline(dest, yC, xA >> 16, xC >> 16, colorA >> 7, colorC >> 7);
                xA += xStepBC;
                xC += xStepAC;
                colorA += colorStepBC;
                colorC += colorStepAC;
            }

            while (--yA >= 0) {
                drawGouraudScanline(dest, yC, xB >> 16, xC >> 16, colorB >> 7, colorC >> 7);
                xB += xStepAB;
                xC += xStepAC;
                colorB += colorStepAB;
                colorC += colorStepAC;
                yC += width;
            }
            return;
        }
        yA -= yB;
        yB -= yC;
        for (yC = offsets[yC]; --yB >= 0; yC += width) {
            drawGouraudScanline(dest, yC, xC >> 16, xA >> 16, colorC >> 7, colorA >> 7);
            xA += xStepBC;
            xC += xStepAC;
            colorA += colorStepBC;
            colorC += colorStepAC;
        }

        while (--yA >= 0) {
            drawGouraudScanline(dest, yC, xC >> 16, xB >> 16, colorC >> 7, colorB >> 7);
            xB += xStepAB;
            xC += xStepAC;
            colorB += colorStepAB;
            colorC += colorStepAC;
            yC += width;
        }
    }

    public static void drawGouraudScanline(int[] dst, int dstOff, int leftX, int rightX, int leftColor, int rightColor) {
        int color;
        int length;

        if (jagged) {
            int colorStep;

            if (testX) {
                if (rightX - leftX > 3)
                    colorStep = (rightColor - leftColor) / (rightX - leftX);
                else
                    colorStep = 0;
                if (rightX > Draw2D.rightX)
                    rightX = Draw2D.rightX;
                if (leftX < 0) {
                    leftColor -= leftX * colorStep;
                    leftX = 0;
                }
                if (leftX >= rightX)
                    return;
                dstOff += leftX;
                length = rightX - leftX >> 2;
                colorStep <<= 2;
            } else {
                if (leftX >= rightX)
                    return;
                dstOff += leftX;
                length = rightX - leftX >> 2;
                if (length > 0)
                    colorStep = (rightColor - leftColor) * reciprical15[length] >> 15;
                else
                    colorStep = 0;
            }
            if (alpha == 0) {
                while (--length >= 0) {
                    color = palette[leftColor >> 8];
                    leftColor += colorStep;
                    dst[dstOff++] = color;
                    dst[dstOff++] = color;
                    dst[dstOff++] = color;
                    dst[dstOff++] = color;
                }
                length = rightX - leftX & 3;
                if (length > 0) {
                    color = palette[leftColor >> 8];
                    do {
                        dst[dstOff++] = color;
                    } while (--length > 0);
                    return;
                }
            } else {
                int alpha = Draw3D.alpha;
                int invAlpha = 256 - Draw3D.alpha;
                while (--length >= 0) {
                    color = palette[leftColor >> 8];
                    leftColor += colorStep;
                    color = ((color & 0xff00ff) * invAlpha >> 8 & 0xff00ff) + ((color & 0xff00) * invAlpha >> 8 & 0xff00);
                    for (int i = 0; i < 4; ++i) {
                        dst[dstOff] = color + ((dst[dstOff] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((dst[dstOff] & 0xff00) * alpha >> 8 & 0xff00);
                        dstOff++; // incrementing here instead fixes a transparency bug
                    }
                }
                length = rightX - leftX & 3;
                if (length > 0) {
                    color = palette[leftColor >> 8];
                    color = ((color & 0xff00ff) * invAlpha >> 8 & 0xff00ff) + ((color & 0xff00) * invAlpha >> 8 & 0xff00);
                    do {
                        dst[dstOff] = color + ((dst[dstOff] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((dst[dstOff] & 0xff00) * alpha >> 8 & 0xff00);
                        dstOff++; // incrementing here instead fixes a transparency bug
                    } while (--length > 0);
                }
            }
            return;
        }
        if (leftX >= rightX)
            return;
        int colorStep = (rightColor - leftColor) / (rightX - leftX);
        if (testX) {
            if (rightX > Draw2D.rightX)
                rightX = Draw2D.rightX;
            if (leftX < 0) {
                leftColor -= leftX * colorStep;
                leftX = 0;
            }
            if (leftX >= rightX)
                return;
        }
        dstOff += leftX;
        length = rightX - leftX;
        if (alpha == 0) {
            do {
                dst[dstOff++] = palette[leftColor >> 8];
                leftColor += colorStep;
            } while (--length > 0);
            return;
        }
        int alpha = Draw3D.alpha;
        int invAlpha = 256 - Draw3D.alpha;
        do {
            color = palette[leftColor >> 8];
            leftColor += colorStep;
            color = ((color & 0xff00ff) * invAlpha >> 8 & 0xff00ff) + ((color & 0xff00) * invAlpha >> 8 & 0xff00);
            dst[dstOff] = color + ((dst[dstOff] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((dst[dstOff] & 0xff00) * alpha >> 8 & 0xff00);
            dstOff++;
        } while (--length > 0);
    }

    public static void fillTriangle(int yA, int yB, int yC, int xA, int xB, int xC, int color) {
        int mAB = 0;
        if (yB != yA)
            mAB = (xB - xA << 16) / (yB - yA);
        int mBC = 0;
        if (yC != yB)
            mBC = (xC - xB << 16) / (yC - yB);
        int mCA = 0;
        if (yC != yA)
            mCA = (xA - xC << 16) / (yA - yC);
        if (yA <= yB && yA <= yC) {
            if (yA >= bottom)
                return;
            if (yB > bottom)
                yB = bottom;
            if (yC > bottom)
                yC = bottom;
            if (yB < yC) {
                xC = xA <<= 16;
                if (yA < 0) {
                    xC -= mCA * yA;
                    xA -= mAB * yA;
                    yA = 0;
                }
                xB <<= 16;
                if (yB < 0) {
                    xB -= mBC * yB;
                    yB = 0;
                }
                if (yA != yB && mCA < mAB || yA == yB && mCA > mBC) {
                    yC -= yB;
                    yB -= yA;
                    for (yA = offsets[yA]; --yB >= 0; yA += width) {
                        drawScanline(dest, yA, color, xC >> 16, xA >> 16);
                        xC += mCA;
                        xA += mAB;
                    }

                    while (--yC >= 0) {
                        drawScanline(dest, yA, color, xC >> 16, xB >> 16);
                        xC += mCA;
                        xB += mBC;
                        yA += width;
                    }
                    return;
                }
                yC -= yB;
                yB -= yA;
                for (yA = offsets[yA]; --yB >= 0; yA += width) {
                    drawScanline(dest, yA, color, xA >> 16, xC >> 16);
                    xC += mCA;
                    xA += mAB;
                }

                while (--yC >= 0) {
                    drawScanline(dest, yA, color, xB >> 16, xC >> 16);
                    xC += mCA;
                    xB += mBC;
                    yA += width;
                }
                return;
            }
            xB = xA <<= 16;
            if (yA < 0) {
                xB -= mCA * yA;
                xA -= mAB * yA;
                yA = 0;
            }
            xC <<= 16;
            if (yC < 0) {
                xC -= mBC * yC;
                yC = 0;
            }
            if (yA != yC && mCA < mAB || yA == yC && mBC > mAB) {
                yB -= yC;
                yC -= yA;
                for (yA = offsets[yA]; --yC >= 0; yA += width) {
                    drawScanline(dest, yA, color, xB >> 16, xA >> 16);
                    xB += mCA;
                    xA += mAB;
                }

                while (--yB >= 0) {
                    drawScanline(dest, yA, color, xC >> 16, xA >> 16);
                    xC += mBC;
                    xA += mAB;
                    yA += width;
                }
                return;
            }
            yB -= yC;
            yC -= yA;
            for (yA = offsets[yA]; --yC >= 0; yA += width) {
                drawScanline(dest, yA, color, xA >> 16, xB >> 16);
                xB += mCA;
                xA += mAB;
            }

            while (--yB >= 0) {
                drawScanline(dest, yA, color, xA >> 16, xC >> 16);
                xC += mBC;
                xA += mAB;
                yA += width;
            }
            return;
        }
        if (yB <= yC) {
            if (yB >= bottom)
                return;
            if (yC > bottom)
                yC = bottom;
            if (yA > bottom)
                yA = bottom;
            if (yC < yA) {
                xA = xB <<= 16;
                if (yB < 0) {
                    xA -= mAB * yB;
                    xB -= mBC * yB;
                    yB = 0;
                }
                xC <<= 16;
                if (yC < 0) {
                    xC -= mCA * yC;
                    yC = 0;
                }
                if (yB != yC && mAB < mBC || yB == yC && mAB > mCA) {
                    yA -= yC;
                    yC -= yB;
                    for (yB = offsets[yB]; --yC >= 0; yB += width) {
                        drawScanline(dest, yB, color, xA >> 16, xB >> 16);
                        xA += mAB;
                        xB += mBC;
                    }

                    while (--yA >= 0) {
                        drawScanline(dest, yB, color, xA >> 16, xC >> 16);
                        xA += mAB;
                        xC += mCA;
                        yB += width;
                    }
                    return;
                }
                yA -= yC;
                yC -= yB;
                for (yB = offsets[yB]; --yC >= 0; yB += width) {
                    drawScanline(dest, yB, color, xB >> 16, xA >> 16);
                    xA += mAB;
                    xB += mBC;
                }

                while (--yA >= 0) {
                    drawScanline(dest, yB, color, xC >> 16, xA >> 16);
                    xA += mAB;
                    xC += mCA;
                    yB += width;
                }
                return;
            }
            xC = xB <<= 16;
            if (yB < 0) {
                xC -= mAB * yB;
                xB -= mBC * yB;
                yB = 0;
            }
            xA <<= 16;
            if (yA < 0) {
                xA -= mCA * yA;
                yA = 0;
            }
            if (mAB < mBC) {
                yC -= yA;
                yA -= yB;
                for (yB = offsets[yB]; --yA >= 0; yB += width) {
                    drawScanline(dest, yB, color, xC >> 16, xB >> 16);
                    xC += mAB;
                    xB += mBC;
                }

                while (--yC >= 0) {
                    drawScanline(dest, yB, color, xA >> 16, xB >> 16);
                    xA += mCA;
                    xB += mBC;
                    yB += width;
                }
                return;
            }
            yC -= yA;
            yA -= yB;
            for (yB = offsets[yB]; --yA >= 0; yB += width) {
                drawScanline(dest, yB, color, xB >> 16, xC >> 16);
                xC += mAB;
                xB += mBC;
            }

            while (--yC >= 0) {
                drawScanline(dest, yB, color, xB >> 16, xA >> 16);
                xA += mCA;
                xB += mBC;
                yB += width;
            }
            return;
        }
        if (yC >= bottom)
            return;
        if (yA > bottom)
            yA = bottom;
        if (yB > bottom)
            yB = bottom;
        if (yA < yB) {
            xB = xC <<= 16;
            if (yC < 0) {
                xB -= mBC * yC;
                xC -= mCA * yC;
                yC = 0;
            }
            xA <<= 16;
            if (yA < 0) {
                xA -= mAB * yA;
                yA = 0;
            }
            if (mBC < mCA) {
                yB -= yA;
                yA -= yC;
                for (yC = offsets[yC]; --yA >= 0; yC += width) {
                    drawScanline(dest, yC, color, xB >> 16, xC >> 16);
                    xB += mBC;
                    xC += mCA;
                }

                while (--yB >= 0) {
                    drawScanline(dest, yC, color, xB >> 16, xA >> 16);
                    xB += mBC;
                    xA += mAB;
                    yC += width;
                }
                return;
            }
            yB -= yA;
            yA -= yC;
            for (yC = offsets[yC]; --yA >= 0; yC += width) {
                drawScanline(dest, yC, color, xC >> 16, xB >> 16);
                xB += mBC;
                xC += mCA;
            }

            while (--yB >= 0) {
                drawScanline(dest, yC, color, xA >> 16, xB >> 16);
                xB += mBC;
                xA += mAB;
                yC += width;
            }
            return;
        }
        xA = xC <<= 16;
        if (yC < 0) {
            xA -= mBC * yC;
            xC -= mCA * yC;
            yC = 0;
        }
        xB <<= 16;
        if (yB < 0) {
            xB -= mAB * yB;
            yB = 0;
        }
        if (mBC < mCA) {
            yA -= yB;
            yB -= yC;
            for (yC = offsets[yC]; --yB >= 0; yC += width) {
                drawScanline(dest, yC, color, xA >> 16, xC >> 16);
                xA += mBC;
                xC += mCA;
            }

            while (--yA >= 0) {
                drawScanline(dest, yC, color, xB >> 16, xC >> 16);
                xB += mAB;
                xC += mCA;
                yC += width;
            }
            return;
        }
        yA -= yB;
        yB -= yC;
        for (yC = offsets[yC]; --yB >= 0; yC += width) {
            drawScanline(dest, yC, color, xC >> 16, xA >> 16);
            xA += mBC;
            xC += mCA;
        }

        while (--yA >= 0) {
            drawScanline(dest, yC, color, xC >> 16, xB >> 16);
            xB += mAB;
            xC += mCA;
            yC += width;
        }
    }

    public static void drawScanline(int[] pixels, int offset, int color, int xA, int xB) {
        if (testX) {
            if (xB > rightX)
                xB = rightX;
            if (xA < 0)
                xA = 0;
        }
        if (xA >= xB)
            return;
        offset += xA;
        int length = xB - xA >> 2;
        if (alpha == 0) {
            while (--length >= 0) {
                pixels[offset++] = color;
                pixels[offset++] = color;
                pixels[offset++] = color;
                pixels[offset++] = color;
            }
            for (length = xB - xA & 3; --length >= 0; )
                pixels[offset++] = color;

            return;
        }
        int alpha = Draw3D.alpha;
        int invAlpha = 256 - Draw3D.alpha;
        color = ((color & 0xff00ff) * invAlpha >> 8 & 0xff00ff) + ((color & 0xff00) * invAlpha >> 8 & 0xff00);
        while (--length >= 0) {
            pixels[offset++] = color + ((pixels[offset] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((pixels[offset] & 0xff00) * alpha >> 8 & 0xff00);
            pixels[offset++] = color + ((pixels[offset] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((pixels[offset] & 0xff00) * alpha >> 8 & 0xff00);
            pixels[offset++] = color + ((pixels[offset] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((pixels[offset] & 0xff00) * alpha >> 8 & 0xff00);
            pixels[offset++] = color + ((pixels[offset] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((pixels[offset] & 0xff00) * alpha >> 8 & 0xff00);
        }
        for (length = xB - xA & 3; --length >= 0; )
            pixels[offset++] = color + ((pixels[offset] & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((pixels[offset] & 0xff00) * alpha >> 8 & 0xff00);
    }
}
