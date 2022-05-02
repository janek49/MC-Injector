package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundResourcePackPacket implements Packet {
   private ServerboundResourcePackPacket.Action action;

   public ServerboundResourcePackPacket() {
   }

   public ServerboundResourcePackPacket(ServerboundResourcePackPacket.Action action) {
      this.action = action;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.action = (ServerboundResourcePackPacket.Action)friendlyByteBuf.readEnum(ServerboundResourcePackPacket.Action.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleResourcePackResponse(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
