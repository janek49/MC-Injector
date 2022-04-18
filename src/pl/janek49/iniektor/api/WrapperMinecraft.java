package pl.janek49.iniektor.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import pl.janek49.iniektor.agent.Version;

public class WrapperMinecraft implements IWrapper {

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/timer")
    public FieldDefinition timer;

    @ResolveField(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/Minecraft/fontRendererObj")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/fontRenderer")
    public FieldDefinition fontRendererField;
    public FontRenderer fontRenderer;

    @ResolveConstructor(version = Version.MC1_7_10, name = "net/minecraft/client/gui/ScaledResolution", params = {"net/minecraft/client/Minecraft", "I", "I"})
    @ResolveConstructor(version = Version.MC1_6_4, name = "net/minecraft/client/gui/ScaledResolution", params = {"net/minecraft/client/settings/GameSettings", "I", "I"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/client/gui/ScaledResolution", params = "net/minecraft/client/Minecraft")
    public ConstructorDefinition scaledResolution;

    @Override
    public void initWrapper() {
        fontRenderer = fontRendererField.get(getDefaultInstance());
    }

    @Override
    public Minecraft getDefaultInstance() {
        return Minecraft.getMinecraft();
    }

    public ScaledResolution getScaledResolution() {
        Minecraft mc = getDefaultInstance();

        if (Reflector.isOnVersion(Version.MC1_7_10)) {
            return scaledResolution.newType(mc, mc.displayWidth, mc.displayHeight);
        } else if (Reflector.isOnVersion(Version.MC1_6_4)) {
            return scaledResolution.newType(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        } else {
            return new ScaledResolution(mc);
        }
    }


    public Object getTimer() {
        return timer.get(getDefaultInstance());
    }

    public float getTimerSpeed() {
        return Invoker.fromObj(getDefaultInstance()).field(timer).get().field(WrapperMisc.Timer_timerSpeed).getType();
    }

    public void setTimerSpeed(float speed) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_12))
            speed = 50f / speed;

        Invoker.fromObj(getTimer()).field(WrapperMisc.Timer_timerSpeed).set(speed);
    }
}
