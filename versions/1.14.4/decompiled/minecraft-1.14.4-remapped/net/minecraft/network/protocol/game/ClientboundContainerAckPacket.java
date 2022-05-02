package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundContainerAckPacket implements Packet {
   private int containerId;
   private short uid;
   private boolean accepted;

   public ClientboundContainerAckPacket() {
   }

   public ClientboundContainerAckPacket(int containerId, short uid, boolean accepted) {
      this.containerId = containerId;
      this.uid = uid;
      this.accepted = accepted;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleContainerAck(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readUnsignedByte();
      this.uid = friendlyByteBuf.readShort();
      this.accepted = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.uid);
      friendlyByteBuf.writeBoolean(this.accepted);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public short getUid() {
      return this.uid;
   }

   public boolean isAccepted() {
      return this.accepted;
   }
}
