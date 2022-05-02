package net.minecraft.client.gui.chat;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public interface ChatListener {
   void handle(ChatType var1, Component var2);
}
