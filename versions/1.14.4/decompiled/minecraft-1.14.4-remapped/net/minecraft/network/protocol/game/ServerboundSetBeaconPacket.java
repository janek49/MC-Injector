package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSetBeaconPacket implements Packet {
   private int primary;
   private int secondary;

   public ServerboundSetBeaconPacket() {
   }

   public ServerboundSetBeaconPacket(int primary, int secondary) {
      this.primary = primary;
      this.secondary = secondary;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.primary = friendlyByteBuf.readVarInt();
      this.secondary = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.primary);
      friendlyByteBuf.writeVarInt(this.secondary);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetBeaconPacket(this);
   }

   public int getPrimary() {
      return this.primary;
   }

   public int getSecondary() {
      return this.secondary;
   }
}
