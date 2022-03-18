package pl.janek49.iniektor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.ChatComponentText;
import pl.janek49.iniektor.client.hook.Reflector;

public class IniektorUtil {
    public static void showChatMessage(String text) {
        Reflector.PLAYER.getPlayerObj().addChatMessage(new ChatComponentText("§7[§cIniektor§7] §r" + text));
    }

    public static void HurtSelf() {
        EntityLivingBase pl = Reflector.PLAYER.getPlayerObj();
        pl.onGround = false;
        double x = pl.posX;
        double y = pl.posY;
        double z = pl.posZ;
        for (int i = 0; i < 10; i++) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.7d, z, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.2d, z, false));
        }
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        pl.onGround = true;
    }
}
