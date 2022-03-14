package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.modules.Module;

public class GuiManager implements EventHandler {

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventRender2D) {
            drawGuiOverlay(((EventRender2D) event).getGui());
        }
    }

    private void drawGuiOverlay(GuiIngame gui) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        gui.drawCenteredString(gui.getFontRenderer(), "Iniektor v0.1", res.getScaledWidth() / 2, 2, 0xFF0000);

        int start = 2;
        for (Module m : IniektorClient.INSTANCE.moduleManager.modules) {
            if (m.isEnabled) {
                gui.drawString(gui.getFontRenderer(), m.name, 2, start, 0x00FF00);
                start += gui.getFontRenderer().FONT_HEIGHT;
            }
        }
    }
}
