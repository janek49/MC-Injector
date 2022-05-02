package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundClientCommandPacket implements Packet {
   private ServerboundClientCommandPacket.Action action;

   public ServerboundClientCommandPacket() {
   }

   public ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action action) {
      this.action = action;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.action = (ServerboundClientCommandPacket.Action)friendlyByteBuf.readEnum(ServerboundClientCommandPacket.Action.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleClientCommand(this);
   }

   public ServerboundClientCommandPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS;
   }
}
