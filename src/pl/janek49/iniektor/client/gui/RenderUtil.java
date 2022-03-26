package pl.janek49.iniektor.client.gui;

import java.awt.*;

public class RenderUtil {
    public static int argbToLong(int a, int r, int g, int b) {
        int al = (a << 24) & 0xFF000000;
        int rl = (r << 16) & 0x00FF0000;
        int gl = (g << 8) & 0x0000FF00;
        int bl = b & 0x000000FF;
        return al | rl | gl | bl;
    }

    public static int getArgbFromColor(Color color) {
        return argbToLong(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color getNextColor(Color color, float incH, float incS, float incB) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[0] = normalizeBetween(hsb[0] + incH, 0f, 1f);
        hsb[1] = normalizeBetween(hsb[1] + incS, 0f, 1f);
        hsb[2] = normalizeBetween(hsb[2] + incB, 0f, 1f);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static float normalizeBetween(float target, float min, float max) {
        if (target < min) {
            while (target < min)
                target += max;
        } else if (target > max) {
            while (target > max)
                target -= max;
        }
        return target;
    }
}
