package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundHorseScreenOpenPacket implements Packet {
   private int containerId;
   private int size;
   private int entityId;

   public ClientboundHorseScreenOpenPacket() {
   }

   public ClientboundHorseScreenOpenPacket(int containerId, int size, int entityId) {
      this.containerId = containerId;
      this.size = size;
      this.entityId = entityId;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleHorseScreenOpen(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readUnsignedByte();
      this.size = friendlyByteBuf.readVarInt();
      this.entityId = friendlyByteBuf.readInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeVarInt(this.size);
      friendlyByteBuf.writeInt(this.entityId);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSize() {
      return this.size;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
