package net.minecraft.client.gui.chat;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.text2speech.Narrator;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class NarratorChatListener implements ChatListener {
   public static final Component NO_TITLE = new TextComponent("");
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public void handle(ChatType chatType, Component component) {
      NarratorStatus var3 = getStatus();
      if(var3 != NarratorStatus.OFF && this.narrator.active()) {
         if(var3 == NarratorStatus.ALL || var3 == NarratorStatus.CHAT && chatType == ChatType.CHAT || var3 == NarratorStatus.SYSTEM && chatType == ChatType.SYSTEM) {
            Component var4;
            if(component instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)component).getKey())) {
               var4 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)component).getArgs());
            } else {
               var4 = component;
            }

            this.doSay(chatType.shouldInterrupt(), var4.getString());
         }

      }
   }

   public void sayNow(String string) {
      NarratorStatus var2 = getStatus();
      if(this.narrator.active() && var2 != NarratorStatus.OFF && var2 != NarratorStatus.CHAT && !string.isEmpty()) {
         this.narrator.clear();
         this.doSay(true, string);
      }

   }

   private static NarratorStatus getStatus() {
      return Minecraft.getInstance().options.narratorStatus;
   }

   private void doSay(boolean var1, String string) {
      if(SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", string);
      }

      this.narrator.say(string, var1);
   }

   public void updateNarratorStatus(NarratorStatus narratorStatus) {
      this.clear();
      this.narrator.say((new TranslatableComponent("options.narrator", new Object[0])).getString() + " : " + (new TranslatableComponent(narratorStatus.getKey(), new Object[0])).getString(), true);
      ToastComponent var2 = Minecraft.getInstance().getToasts();
      if(this.narrator.active()) {
         if(narratorStatus == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), (Component)null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled", new Object[0]), new TranslatableComponent(narratorStatus.getKey(), new Object[0]));
         }
      } else {
         SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), new TranslatableComponent("options.narrator.notavailable", new Object[0]));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if(getStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void destroy() {
      this.narrator.destroy();
   }
}
