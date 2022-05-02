package net.minecraft.network.protocol.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet {
   private boolean reset;
   private Map added;
   private Set removed;
   private Map progress;

   public ClientboundUpdateAdvancementsPacket() {
   }

   public ClientboundUpdateAdvancementsPacket(boolean reset, Collection collection, Set removed, Map map) {
      this.reset = reset;
      this.added = Maps.newHashMap();

      for(Advancement var6 : collection) {
         this.added.put(var6.getId(), var6.deconstruct());
      }

      this.removed = removed;
      this.progress = Maps.newHashMap(map);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleUpdateAdvancementsPacket(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.reset = friendlyByteBuf.readBoolean();
      this.added = Maps.newHashMap();
      this.removed = Sets.newLinkedHashSet();
      this.progress = Maps.newHashMap();
      int var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = friendlyByteBuf.readResourceLocation();
         Advancement.Builder var5 = Advancement.Builder.fromNetwork(friendlyByteBuf);
         this.added.put(var4, var5);
      }

      var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = friendlyByteBuf.readResourceLocation();
         this.removed.add(var4);
      }

      var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = friendlyByteBuf.readResourceLocation();
         this.progress.put(var4, AdvancementProgress.fromNetwork(friendlyByteBuf));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBoolean(this.reset);
      friendlyByteBuf.writeVarInt(this.added.size());

      for(Entry<ResourceLocation, Advancement.Builder> var3 : this.added.entrySet()) {
         ResourceLocation var4 = (ResourceLocation)var3.getKey();
         Advancement.Builder var5 = (Advancement.Builder)var3.getValue();
         friendlyByteBuf.writeResourceLocation(var4);
         var5.serializeToNetwork(friendlyByteBuf);
      }

      friendlyByteBuf.writeVarInt(this.removed.size());

      for(ResourceLocation var3 : this.removed) {
         friendlyByteBuf.writeResourceLocation(var3);
      }

      friendlyByteBuf.writeVarInt(this.progress.size());

      for(Entry<ResourceLocation, AdvancementProgress> var3 : this.progress.entrySet()) {
         friendlyByteBuf.writeResourceLocation((ResourceLocation)var3.getKey());
         ((AdvancementProgress)var3.getValue()).serializeToNetwork(friendlyByteBuf);
      }

   }

   public Map getAdded() {
      return this.added;
   }

   public Set getRemoved() {
      return this.removed;
   }

   public Map getProgress() {
      return this.progress;
   }

   public boolean shouldReset() {
      return this.reset;
   }
}
