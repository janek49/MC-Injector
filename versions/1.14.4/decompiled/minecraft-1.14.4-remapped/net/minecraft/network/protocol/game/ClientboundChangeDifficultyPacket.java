package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.Difficulty;

public class ClientboundChangeDifficultyPacket implements Packet {
   private Difficulty difficulty;
   private boolean locked;

   public ClientboundChangeDifficultyPacket() {
   }

   public ClientboundChangeDifficultyPacket(Difficulty difficulty, boolean locked) {
      this.difficulty = difficulty;
      this.locked = locked;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleChangeDifficulty(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.difficulty = Difficulty.byId(friendlyByteBuf.readUnsignedByte());
      this.locked = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.difficulty.getId());
      friendlyByteBuf.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
