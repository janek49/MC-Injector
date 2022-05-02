package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundAcceptTeleportationPacket implements Packet {
   private int id;

   public ServerboundAcceptTeleportationPacket() {
   }

   public ServerboundAcceptTeleportationPacket(int id) {
      this.id = id;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
