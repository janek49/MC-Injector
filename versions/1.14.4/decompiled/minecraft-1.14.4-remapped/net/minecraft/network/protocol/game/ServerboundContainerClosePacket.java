package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundContainerClosePacket implements Packet {
   private int containerId;

   public ServerboundContainerClosePacket() {
   }

   public ServerboundContainerClosePacket(int containerId) {
      this.containerId = containerId;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleContainerClose(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
   }
}
