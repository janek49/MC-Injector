package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.MethodDefinition;
import pl.janek49.iniektor.api.ResolveMethod;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "com/mojang/blaze3d/platform/Window")
public class Blaze3DWindow extends ClassImitator {
    public static ClassInformation target;

    @ResolveMethod(name = "getGuiScaledHeight", descriptor = "()I")
    private static MethodDefinition getGuiScaledHeight;

    @ResolveMethod(name = "getGuiScaledWidth", descriptor = "()I")
    private static MethodDefinition getGuiScaledWidth;

    @ResolveMethod(name = "getGuiScale", descriptor = "()D")
    private static MethodDefinition getGuiScale;

    private Blaze3DWindow(){}

    public Blaze3DWindow(Object instance){
        super(instance);
    }

    public int getScaledWidth(){
        return Blaze3DWindow.getGuiScaledWidth.invokeType(getInstanceBehind());
    }

    public int getScaledHeight(){
        return Blaze3DWindow.getGuiScaledHeight.invokeType(getInstanceBehind());
    }

    public double getScaleFactor(){
        return Blaze3DWindow.getGuiScale.invokeType(getInstanceBehind());
    }
}
