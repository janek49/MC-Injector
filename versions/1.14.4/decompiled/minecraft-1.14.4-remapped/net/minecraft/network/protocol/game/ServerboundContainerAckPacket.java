package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundContainerAckPacket implements Packet {
   private int containerId;
   private short uid;
   private boolean accepted;

   public ServerboundContainerAckPacket() {
   }

   public ServerboundContainerAckPacket(int containerId, short uid, boolean accepted) {
      this.containerId = containerId;
      this.uid = uid;
      this.accepted = accepted;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleContainerAck(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.uid = friendlyByteBuf.readShort();
      this.accepted = friendlyByteBuf.readByte() != 0;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.uid);
      friendlyByteBuf.writeByte(this.accepted?1:0);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public short getUid() {
      return this.uid;
   }
}
