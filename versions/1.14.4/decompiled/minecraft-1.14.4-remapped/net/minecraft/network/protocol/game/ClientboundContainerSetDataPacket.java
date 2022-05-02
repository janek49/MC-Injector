package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundContainerSetDataPacket implements Packet {
   private int containerId;
   private int id;
   private int value;

   public ClientboundContainerSetDataPacket() {
   }

   public ClientboundContainerSetDataPacket(int containerId, int id, int value) {
      this.containerId = containerId;
      this.id = id;
      this.value = value;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleContainerSetData(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readUnsignedByte();
      this.id = friendlyByteBuf.readShort();
      this.value = friendlyByteBuf.readShort();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.id);
      friendlyByteBuf.writeShort(this.value);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getId() {
      return this.id;
   }

   public int getValue() {
      return this.value;
   }
}
