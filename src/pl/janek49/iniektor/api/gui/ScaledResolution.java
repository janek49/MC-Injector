package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.reflection.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/ScaledResolution")
public class ScaledResolution extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(version = Version.MC1_7_10, params = {"net/minecraft/client/Minecraft", "I", "I"})
    @ResolveConstructor(version = Version.MC1_6_4, params = {"net/minecraft/client/settings/GameSettings", "I", "I"})
    @ResolveConstructor(version = Version.DEFAULT, params = "net/minecraft/client/Minecraft")
    public static ConstructorDefinition constructor;

    @ResolveMethod(name = "getScaledHeight", descriptor = "()I")
    private static MethodDefinition getScaledHeight;

    @ResolveMethod(name = "getScaledWidth", descriptor = "()I")
    private static MethodDefinition getScaledWidth;

    @ResolveMethod(name = "getScaleFactor", descriptor = "()I")
    private static MethodDefinition getScaleFactor;

    private Object instance;
    public ScaledResolution(Object instance){
        this.instance = instance;
    }

    private ScaledResolution(){}

    public static ScaledResolution createInstance() {
        if (Reflector.isOnVersion(Version.MC1_7_10)) {
            return new ScaledResolution(constructor.newInstance(Minecraft.getInstanceObj(), Minecraft.displayWidth.get(), Minecraft.displayHeight.get()));
        } else if (Reflector.isOnVersion(Version.MC1_6_4)) {
            return new ScaledResolution(constructor.newInstance(Minecraft.gameSettings.get(), Minecraft.displayWidth.get(), Minecraft.displayHeight.get()));
        } else {
            return new ScaledResolution(constructor.newInstance(Minecraft.getInstanceObj()));
        }
    }

    public int getScaledWidth(){
        return ScaledResolution.getScaledWidth.invokeType(instance);
    }

    public int getScaledHeight(){
        return ScaledResolution.getScaledHeight.invokeType(instance);
    }

    public int getScaleFactor(){
        return ScaledResolution.getScaleFactor.invokeType(instance);
    }


}
