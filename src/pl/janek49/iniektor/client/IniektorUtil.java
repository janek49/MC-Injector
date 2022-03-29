package pl.janek49.iniektor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.WrapperChat;
import pl.janek49.iniektor.client.hook.WrapperMisc;

public class IniektorUtil {
    public static void showChatMessage(String text) {
        try {
            String val = "§7[§cIniektor§7] §r" + text;
            if (Reflector.isOnOrAbvVersion(Version.MC1_7_10)) {
                WrapperChat.addChatMessage.invoke(Reflector.PLAYER.getDefaultInstance(), WrapperChat.TextComponentString.newInstance(val));
            } else {
                WrapperChat.addChatMessage.invoke(Reflector.PLAYER.getDefaultInstance(), val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void playPressSound(){
        try {
            WrapperMisc.GuiButton_playPressSound.invoke(new GuiButton(0,0,0,""), Minecraft.getMinecraft().getSoundHandler());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
