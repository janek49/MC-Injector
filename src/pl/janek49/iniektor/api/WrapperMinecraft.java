package pl.janek49.iniektor.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import pl.janek49.iniektor.agent.Version;

public class WrapperMinecraft implements IWrapper {

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/timer")
    public FieldDefinition timer;

    @ResolveField(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/Minecraft/fontRendererObj")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/fontRenderer")
    public FieldDefinition fontRendererField;
    public FontRenderer fontRenderer;


    @Override
    public void initWrapper() {
        Minecraft mc = Minecraft.getMinecraft();

        fontRenderer = fontRendererField.get(mc);
    }

    @Override
    public Object getDefaultInstance() {
        return Minecraft.getMinecraft();
    }



    public ScaledResolution getScaledResolution() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (Reflector.isOnVersion(Version.MC1_7_10)) {
                return ScaledResolution.class.getDeclaredConstructor(Minecraft.class, int.class, int.class).newInstance(mc, mc.displayWidth, mc.displayHeight);
            } else if (Reflector.isOnOrBlwVersion(Version.MC1_6_4)) {
                return ScaledResolution.class.getDeclaredConstructor(GameSettings.class, int.class, int.class).newInstance(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            } else {
                return new ScaledResolution(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public Object getTimer() {
        return timer.get(getDefaultInstance());
    }

    public float getTimerSpeed(){
        return WrapperMisc.Timer_timerSpeed.get(getTimer());
    }

    public void setTimerSpeed(float speed) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_12)) {
            WrapperMisc.Timer_timerSpeed.set(getTimer(), 50f / speed);
        } else {
            WrapperMisc.Timer_timerSpeed.set(getTimer(), speed);
        }
    }
}
