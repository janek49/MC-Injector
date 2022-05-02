package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundContainerButtonClickPacket implements Packet {
   private int containerId;
   private int buttonId;

   public ServerboundContainerButtonClickPacket() {
   }

   public ServerboundContainerButtonClickPacket(int containerId, int buttonId) {
      this.containerId = containerId;
      this.buttonId = buttonId;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleContainerButtonClick(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.buttonId = friendlyByteBuf.readByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeByte(this.buttonId);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}
