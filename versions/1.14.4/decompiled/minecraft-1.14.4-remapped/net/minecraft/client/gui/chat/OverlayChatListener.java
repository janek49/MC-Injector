package net.minecraft.client.gui.chat;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class OverlayChatListener implements ChatListener {
   private final Minecraft minecraft;

   public OverlayChatListener(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void handle(ChatType chatType, Component component) {
      this.minecraft.gui.setOverlayMessage(component, false);
   }
}
