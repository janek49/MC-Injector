package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Timer;
import pl.janek49.iniektor.agent.Version;

import java.sql.Ref;

public class WrapperMinecraft implements IWrapper {
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/timer")
    public String TIMER_FIELD;
    public Timer minecraftTimer;

    @ResolveField(version = Version.MC1_7_10, name = "net/minecraft/client/Minecraft/fontRenderer")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/fontRendererObj")
    public String FONTRENDER_FIELD;
    public FontRenderer fontRenderer;

    public ScaledResolution getScaledResolution() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (Reflector.MCP_VERSION == Version.MC1_7_10) {
                return ScaledResolution.class.getDeclaredConstructor(Minecraft.class, int.class, int.class).newInstance(mc, mc.displayWidth, mc.displayHeight);
            } else {
                return new ScaledResolution(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initWrapper() {
        Minecraft mc = Minecraft.getMinecraft();

        minecraftTimer = Reflector.getPrivateFieldValue(Minecraft.class, mc, TIMER_FIELD);
        fontRenderer = Reflector.getDeclaredFieldValue(Minecraft.class, mc, FONTRENDER_FIELD);
    }
}
