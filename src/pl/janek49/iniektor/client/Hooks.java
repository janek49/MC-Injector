package pl.janek49.iniektor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import pl.janek49.iniektor.agent.Logger;

public class Hooks {

    public static String GetFullClassNameForJA() {
        return Hooks.class.getName().replace("/", ".");
    }

    public static void HookRenderInGameOverlay() {
        Logger.log("Test");
        GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        gui.drawCenteredString(gui.getFontRenderer(), "Iniektor v0.1", res.getScaledWidth() / 2, 0, 0xFF0000);
    }
}
