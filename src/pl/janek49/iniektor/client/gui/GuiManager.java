package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.Module;

import java.sql.Ref;

public class GuiManager implements EventHandler {

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventRender2D) {
            drawGuiOverlay(((EventRender2D) event).getGui());
        }
    }

    private void drawGuiOverlay(GuiIngame gui) {
        FontRenderer fontR = Reflector.MC.fontRenderer;
        ScaledResolution res = Reflector.MC.getScaledResolution();

        gui.drawCenteredString(fontR, "Iniektor v0.1", res.getScaledWidth() / 2, 2, 0xFF0000);

        int start = 2;
        for (Module m : IniektorClient.INSTANCE.moduleManager.modules) {
            if (m.isEnabled) {
                gui.drawString(fontR, m.name, 2, start, 0x00FF00);
                start += fontR.FONT_HEIGHT;
            }
        }
    }
}
