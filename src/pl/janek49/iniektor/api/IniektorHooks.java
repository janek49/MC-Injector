package pl.janek49.iniektor.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.network.Packet;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventManager;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventPacketReceived;
import pl.janek49.iniektor.client.events.impl.EventRender2D;

public class IniektorHooks {

    public static void HookRenderInGameOverlay(Object gui) {
        try {
            IniektorClient.INSTANCE.eventManager.fireEvent(new EventGameTick());
            IniektorClient.INSTANCE.eventManager.fireEvent(new EventRender2D((GuiIngame) gui));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static void HookGameLoop() {
        try {
            if (IniektorClient.INSTANCE == null) {
                new IniektorClient();
            } else {
                IniektorClient.INSTANCE.onGameTick();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static boolean HookCancelReceivedPacket(Object packet) {
        EventPacketReceived event = new EventPacketReceived((Packet) packet);
        IniektorClient.INSTANCE.eventManager.fireEvent(event);
        return event.cancel;
    }

    public static boolean GuiChatHook(String text, boolean bool) {
        try {
            if (text.startsWith(".")) {
                IniektorClient.INSTANCE.moduleManager.processChatCommand(text);
                WrapperChat.addToSentMessages.invoke(WrapperChat.getChatGUI.invoke(Minecraft.getMinecraft().ingameGUI), text);
                return true;
            }
            return false;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
