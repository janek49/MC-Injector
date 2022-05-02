package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class GuiMessage {
   private final int addedTime;
   private final Component message;
   private final int id;

   public GuiMessage(int addedTime, Component message, int id) {
      this.message = message;
      this.addedTime = addedTime;
      this.id = id;
   }

   public Component getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}
