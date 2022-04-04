package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.modules.Module;

import java.awt.*;
import java.util.HashMap;

public class GuiManager implements EventHandler {

    public HashMap<Integer, UnicodeFontRenderer> fontMap = new HashMap<>();

    public GuiManager() {
    }


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
                gui.drawString(fontR, m.name, 2, start, 0xEEEEEE);
                start += fontR.FONT_HEIGHT;
            }
        }
    }

    public UnicodeFontRenderer getFontForScale(int scale) {
        if (fontMap.containsKey(scale))
            return fontMap.get(scale);
        else {
            UnicodeFontRenderer ufr = new UnicodeFontRenderer(new Font("Arial", Font.PLAIN, 11 * scale));
            fontMap.put(scale, ufr);
            return ufr;
        }
    }

    public UnicodeFontRenderer getDefaultFont() {
        return getFontForScale(Reflector.MC.getScaledResolution().getScaleFactor());
    }

    public float getFontScale() {
        return 2f / ((float) Reflector.MC.getScaledResolution().getScaleFactor());
    }
}
