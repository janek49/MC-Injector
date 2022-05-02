package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundLockDifficultyPacket implements Packet {
   private boolean locked;

   public ServerboundLockDifficultyPacket() {
   }

   public ServerboundLockDifficultyPacket(boolean locked) {
      this.locked = locked;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleLockDifficulty(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.locked = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }
}
