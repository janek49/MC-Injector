package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.client.Minecraft;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/gui/ScaledResolution")
public class ScaledResolution extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(version = Version.MC1_7_10, params = {"net/minecraft/client/Minecraft", "I", "I"})
    @ResolveConstructor(version = Version.MC1_6_4, params = {"net/minecraft/client/settings/GameSettings", "I", "I"})
    @ResolveConstructor(version = Version.DEFAULT, params = "net/minecraft/client/Minecraft")
    public static ConstructorDefinition constructor;

    @ResolveMethod(name = "getScaledHeight", descriptor = "()I")
    private static MethodDefinition getScaledHeight;

    @ResolveMethod(name = "getScaledHeight", descriptor = "()I")
    private static MethodDefinition getScaledWidth;

    @ResolveMethod(name = "getScaleFactor", descriptor = "()D")
    private static MethodDefinition getScaleFactor;

    private Object instance;
    public ScaledResolution(Object instance){
        this.instance = instance;
    }

    private ScaledResolution(){}

    public static ScaledResolution createInstance() {
        if (Reflector.isOnVersion(Version.MC1_7_10)) {
            return new ScaledResolution(constructor.newInstance(Minecraft.getMinecraft(), Minecraft.displayWidth.get(), Minecraft.displayHeight.get()));
        } else if (Reflector.isOnVersion(Version.MC1_6_4)) {
            return new ScaledResolution(constructor.newInstance(Minecraft.gameSettings.get(), Minecraft.displayWidth.get(), Minecraft.displayHeight.get()));
        } else {
            return new ScaledResolution(constructor.newInstance(Minecraft.getMinecraft()));
        }
    }

    public int getScaledWidth(){
        return ScaledResolution.getScaledWidth.invokeType(instance);
    }

    public int getScaledHeight(){
        return ScaledResolution.getScaledHeight.invokeType(instance);
    }

    public double getScaleFactor(){
        return ScaledResolution.getScaleFactor.invokeType(instance);
    }


}
