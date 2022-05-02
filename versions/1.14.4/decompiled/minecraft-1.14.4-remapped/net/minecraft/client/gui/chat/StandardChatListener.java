package net.minecraft.client.gui.chat;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class StandardChatListener implements ChatListener {
   private final Minecraft minecraft;

   public StandardChatListener(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void handle(ChatType chatType, Component component) {
      this.minecraft.gui.getChat().addMessage(component);
   }
}
