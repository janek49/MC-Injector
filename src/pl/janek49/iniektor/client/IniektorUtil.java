package pl.janek49.iniektor.client;

import pl.janek49.iniektor.client.hook.MCC;
import pl.janek49.iniektor.client.hook.Reflector;

public class IniektorUtil {
    public static void showChatMessage(String text) {
        String val = "§7[§cIniektor§7] §r" + text;
        Reflector.PLAYER.addChatMessage.call(MCC.TextComponentString.newInstance(val));
    }
}
