package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundRenameItemPacket implements Packet {
   private String name;

   public ServerboundRenameItemPacket() {
   }

   public ServerboundRenameItemPacket(String name) {
      this.name = name;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.name = friendlyByteBuf.readUtf(32767);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.name);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
