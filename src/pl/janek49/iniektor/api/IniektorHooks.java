package pl.janek49.iniektor.api;

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
        if (IniektorClient.INSTANCE.eventManager.skipPackets.contains(packet)) {
            IniektorClient.INSTANCE.eventManager.skipPackets.remove(packet);
            return false;
        } else {
            EventPacketReceived event = new EventPacketReceived(packet);
            IniektorClient.INSTANCE.eventManager.fireEvent(event);
            return event.cancel;
        }
    }

    public static boolean GuiChatHook(String text, boolean bool) {
        try {
            if (text.startsWith(".")) {
                IniektorClient.INSTANCE.moduleManager.processChatCommand(text);
                Invoker.fromObj(Reflector.MINECRAFT.ingameGUI.get()).method(WrapperChat.getChatGUI).exec().method(WrapperChat.addToSentMessages).exec(text);
                return true;
            }
            return false;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
