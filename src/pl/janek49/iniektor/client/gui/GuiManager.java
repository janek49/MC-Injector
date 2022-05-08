package pl.janek49.iniektor.client.gui;

import pl.janek49.iniektor.api.wrapper.WrapperResolution;
import pl.janek49.iniektor.api.gui.FontRenderer;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.events.IEvent;
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

    private void drawGuiOverlay(Object gui) {
        FontRenderer.drawCenteredStringWithShadow("Iniektor v0.1", WrapperResolution.getScreenBoundsF()[0] / 2, 2, 0xFF0000);

        int start = 2;
        for (Module m : IniektorClient.INSTANCE.moduleManager.modules) {
            if (m.isEnabled) {
                FontRenderer.drawStringWithShadow(m.name, 2, start, 0xEEEEEE);
                start += FontRenderer.getFontHeight();
            }
        }
    }

    public UnicodeFontRenderer getFontForScale(int base, int scale) {
        int size = base * scale;
        if (fontMap.containsKey(size))
            return fontMap.get(size);
        else {
            UnicodeFontRenderer ufr = new UnicodeFontRenderer(new Font("Arial", Font.PLAIN, size));
            fontMap.put(size, ufr);
            return ufr;
        }
    }

    public UnicodeFontRenderer getDefaultFont() {
        return getFont(11);
    }

    public UnicodeFontRenderer getClickGuiFont() {
        return getFont(9);
    }

    public UnicodeFontRenderer getFont(int size) {
        return getFontForScale(size,(int) WrapperResolution.getScaleFactor());
    }

    public float getFontScale() {
        return (float) (2d / (WrapperResolution.getScaleFactor()));
    }
}
