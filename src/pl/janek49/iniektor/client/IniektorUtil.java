package pl.janek49.iniektor.client;

import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.WrapperChat;

public class IniektorUtil {
    public static void showChatMessage(String text) {
        String val = "§7[§cIniektor§7] §r" + text;
        WrapperChat.addChatMessage.invoke(Reflector.PLAYER.getDefaultInstance(), WrapperChat.TextComponentString.newInstance(val));
    }
}
