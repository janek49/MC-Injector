package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.gui.RenderUtil;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.modules.Module;


public class Hud extends Module implements EventHandler {
    public Hud() {
        super("HUD", Keyboard.KEY_O, Category.RENDER);
        RegisterEvent(EventRender2D.class);
    }

    @Override
    public void onEvent(IEvent event) {
        ScaledResolution sr = Reflector.MC.getScaledResolution();
        String coords = "§7X: §r%.1f §7Y: §r%.1f §7Z: §r%.1f";
        String formatted = String.format(coords, getPlayerObj().posX, getPlayerObj().posY, getPlayerObj().posZ);
        RenderUtil.drawStringWithShadow(Reflector.MC.fontRenderer, formatted, 2, sr.getScaledHeight() - 10, -1);
    }
}
