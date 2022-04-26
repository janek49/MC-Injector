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

    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/Minecraft/getSoundHandler", descriptor = "()Lnet/minecraft/client/audio/SoundHandler;")
    public MethodDefinition getSoundHandler;

    @ResolveField(version = Version.MC1_6_4, name = "net/minecraft/src/Minecraft/sndManager")
    public FieldDefinition mc164soundManager;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/Minecraft/instance")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/theMinecraft")
    public FieldDefinition theMinecraft;

    @Override
    public void initWrapper() {
        fontRenderer = fontRendererField.get(getInstance());
    }

    @Override
    public Minecraft getInstance() {
        return theMinecraft.get(null);
    }

    public ScaledResolution getScaledResolution() {
        Minecraft mc = getInstance();

        if (Reflector.isOnVersion(Version.MC1_7_10)) {
            return scaledResolution.newType(mc, mc.displayWidth, mc.displayHeight);
        } else if (Reflector.isOnVersion(Version.MC1_6_4)) {
            return scaledResolution.newType(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        } else {
            return scaledResolution.newType(mc);
        }
    }


    public Object getTimer() {
        return timer.get(getInstance());
    }

    public float getTimerSpeed() {
        return Invoker.fromObj(getInstance()).field(timer).get().field(WrapperMisc.Timer_timerSpeed).getType();
    }

    public void setTimerSpeed(float speed) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_12))
            speed = 50f / speed;

        Invoker.fromObj(getTimer()).field(WrapperMisc.Timer_timerSpeed).set(speed);
    }
}
