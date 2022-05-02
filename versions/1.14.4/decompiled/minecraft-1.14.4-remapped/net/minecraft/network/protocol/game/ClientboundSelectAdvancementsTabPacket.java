package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet {
   @Nullable
   private ResourceLocation tab;

   public ClientboundSelectAdvancementsTabPacket() {
   }

   public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation tab) {
      this.tab = tab;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSelectAdvancementsTab(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      if(friendlyByteBuf.readBoolean()) {
         this.tab = friendlyByteBuf.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBoolean(this.tab != null);
      if(this.tab != null) {
         friendlyByteBuf.writeResourceLocation(this.tab);
      }

   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }
}
