package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundRemoveEntitiesPacket implements Packet {
   private int[] entityIds;

   public ClientboundRemoveEntitiesPacket() {
   }

   public ClientboundRemoveEntitiesPacket(int... entityIds) {
      this.entityIds = entityIds;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityIds = new int[friendlyByteBuf.readVarInt()];

      for(int var2 = 0; var2 < this.entityIds.length; ++var2) {
         this.entityIds[var2] = friendlyByteBuf.readVarInt();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityIds.length);

      for(int var5 : this.entityIds) {
         friendlyByteBuf.writeVarInt(var5);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleRemoveEntity(this);
   }

   public int[] getEntityIds() {
      return this.entityIds;
   }
}
