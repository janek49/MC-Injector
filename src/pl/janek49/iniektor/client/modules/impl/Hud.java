package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.WrapperResolution;
import pl.janek49.iniektor.api.gui.FontRenderer;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.modules.Module;


public class Hud extends Module implements EventHandler {
    public Hud() {
        super("HUD", Keyboard.KEY_O, Category.RENDER);
        RegisterEvent(EventRender2D.class);
    }

    @Override
    public void onEvent(IEvent event) {
        String coords = "§7X: §r%.1f §7Y: §r%.1f §7Z: §r%.1f";
        String formatted = String.format(coords, getPlayerObj().getPosX(), getPlayerObj().getPosY(), getPlayerObj().getPosZ());
        FontRenderer.drawStringWithShadow(formatted, 2, WrapperResolution.getScreenBounds()[1] - 10, -1);
    }
}
