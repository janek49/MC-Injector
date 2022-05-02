package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ClientAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final AdvancementList advancements = new AdvancementList();
   private final Map progress = Maps.newHashMap();
   @Nullable
   private ClientAdvancements.Listener listener;
   @Nullable
   private Advancement selectedTab;

   public ClientAdvancements(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void update(ClientboundUpdateAdvancementsPacket clientboundUpdateAdvancementsPacket) {
      if(clientboundUpdateAdvancementsPacket.shouldReset()) {
         this.advancements.clear();
         this.progress.clear();
      }

      this.advancements.remove(clientboundUpdateAdvancementsPacket.getRemoved());
      this.advancements.add(clientboundUpdateAdvancementsPacket.getAdded());

      for(Entry<ResourceLocation, AdvancementProgress> var3 : clientboundUpdateAdvancementsPacket.getProgress().entrySet()) {
         Advancement var4 = this.advancements.get((ResourceLocation)var3.getKey());
         if(var4 != null) {
            AdvancementProgress var5 = (AdvancementProgress)var3.getValue();
            var5.update(var4.getCriteria(), var4.getRequirements());
            this.progress.put(var4, var5);
            if(this.listener != null) {
               this.listener.onUpdateAdvancementProgress(var4, var5);
            }

            if(!clientboundUpdateAdvancementsPacket.shouldReset() && var5.isDone() && var4.getDisplay() != null && var4.getDisplay().shouldShowToast()) {
               this.minecraft.getToasts().addToast(new AdvancementToast(var4));
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", var3.getKey());
         }
      }

   }

   public AdvancementList getAdvancements() {
      return this.advancements;
   }

   public void setSelectedTab(@Nullable Advancement selectedTab, boolean var2) {
      ClientPacketListener var3 = this.minecraft.getConnection();
      if(var3 != null && selectedTab != null && var2) {
         var3.send((Packet)ServerboundSeenAdvancementsPacket.openedTab(selectedTab));
      }

      if(this.selectedTab != selectedTab) {
         this.selectedTab = selectedTab;
         if(this.listener != null) {
            this.listener.onSelectedTabChanged(selectedTab);
         }
      }

   }

   public void setListener(@Nullable ClientAdvancements.Listener listener) {
      this.listener = listener;
      this.advancements.setListener(listener);
      if(listener != null) {
         for(Entry<Advancement, AdvancementProgress> var3 : this.progress.entrySet()) {
            listener.onUpdateAdvancementProgress((Advancement)var3.getKey(), (AdvancementProgress)var3.getValue());
         }

         listener.onSelectedTabChanged(this.selectedTab);
      }

   }

   @ClientJarOnly
   public interface Listener extends AdvancementList.Listener {
      void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2);

      void onSelectedTabChanged(@Nullable Advancement var1);
   }
}
