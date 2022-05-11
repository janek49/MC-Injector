package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.wrapper.WrapperChat;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventPacketReceived;
import pl.janek49.iniektor.client.events.impl.EventRender2D;

public class IniektorHooks {

    public static void HookRenderInGameOverlay(Object gui) {
        try {
            IniektorClient.INSTANCE.eventManager.fireEvent(new EventGameTick());
            IniektorClient.INSTANCE.eventManager.fireEvent(new EventRender2D(gui));
        } catch (Throwable ex) {
            Logger.ex(ex);
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
            Logger.ex(ex);
        }
    }

    public static boolean HookCancelReceivedPacket(Object packet) {
        try {
            if (IniektorClient.INSTANCE.eventManager.skipPackets.contains(packet)) {
                IniektorClient.INSTANCE.eventManager.skipPackets.remove(packet);
                return false;
            } else {
                EventPacketReceived event = new EventPacketReceived(packet);
                IniektorClient.INSTANCE.eventManager.fireEvent(event);
                return event.cancel;
            }
        } catch (Throwable ex) {
            Logger.ex(ex);
        }
        return false;
    }

    public static boolean GuiChatHook(String text, boolean bool) {
        if (text != null && text.startsWith(".")) {
            try {
                IniektorClient.INSTANCE.moduleManager.processChatCommand(text);
                WrapperChat.addToSentMessages(text);
            } catch (Throwable ex) {
                Logger.ex(ex);
            }
            return true;
        } else {
            return false;
        }
    }
}
