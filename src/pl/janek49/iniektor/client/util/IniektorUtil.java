package pl.janek49.iniektor.client.util;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.api.WrapperChat;

public class IniektorUtil {
    public static void showChatMessage(String text) {
        try {
            String val = "§7[§cIniektor§7] §r" + text;
            if (Reflector.isOnOrAbvVersion(Version.MC1_7_10)) {
                WrapperChat.addChatMessage.invoke(Reflector.PLAYER.getInstanceBehind(), WrapperChat.TextComponentString.newInstance(val));
            } else {
                WrapperChat.addChatMessage.invoke(Reflector.PLAYER.getInstanceBehind(), val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
