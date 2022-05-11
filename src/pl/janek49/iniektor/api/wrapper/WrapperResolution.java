package pl.janek49.iniektor.api.wrapper;

import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.Blaze3DWindow;
import pl.janek49.iniektor.api.gui.ScaledResolution;
import pl.janek49.iniektor.api.reflection.IWrapper;
import pl.janek49.iniektor.api.reflection.Reflector;

import java.awt.*;

public class WrapperResolution implements IWrapper {


    private static boolean useNewMethod = false;

    public static Dimension getScreenBounds() {
        if (useNewMethod) {
            Blaze3DWindow window = new Blaze3DWindow(Minecraft.window.get());
            return new Dimension(window.getScaledWidth(), window.getScaledHeight());
        } else {
            ScaledResolution scaledRes = ScaledResolution.createInstance();
            return new Dimension(scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
        }
    }

    public static float[] getScreenBoundsF() {
        if (useNewMethod) {
            Blaze3DWindow window = new Blaze3DWindow(Minecraft.window.get());
            return new float[]{window.getScaledWidth(), window.getScaledHeight()};
        } else {
            ScaledResolution scaledRes = ScaledResolution.createInstance();
            return new float[]{scaledRes.getScaledWidth(), scaledRes.getScaledHeight()};
        }
    }

    public static double getScaleFactor() {
        if (useNewMethod) {
            Blaze3DWindow window = new Blaze3DWindow(Minecraft.window.get());
            return window.getScaleFactor();
        } else {
            ScaledResolution scaledRes = ScaledResolution.createInstance();
            return scaledRes.getScaleFactor();
        }
    }

    @Override
    public void initWrapper() {
        useNewMethod = Reflector.USE_NEW_API;
    }

    @Override
    public Object getInstanceBehind() {
        return null;
    }
}
