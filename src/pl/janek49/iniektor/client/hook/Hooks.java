package pl.janek49.iniektor.client.hook;

import net.minecraft.client.gui.GuiIngame;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventRender2D;

public class Hooks {

    public static void HookRenderInGameOverlay(Object gui) {
        IniektorClient.INSTANCE.eventManager.fireEvent(new EventRender2D((GuiIngame) gui));
    }

    public static void HookGameLoop() {
        if (IniektorClient.INSTANCE == null) {
            new IniektorClient();
        } else {
            IniektorClient.INSTANCE.eventManager.fireEvent(new EventGameTick());
        }
    }

    public static String getClassName() {
        return "pl.janek49.iniektor.client.hook.Hooks";
    }
}
