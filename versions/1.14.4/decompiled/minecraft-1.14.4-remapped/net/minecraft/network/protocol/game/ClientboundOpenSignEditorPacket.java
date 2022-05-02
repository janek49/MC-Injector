package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundOpenSignEditorPacket implements Packet {
   private BlockPos pos;

   public ClientboundOpenSignEditorPacket() {
   }

   public ClientboundOpenSignEditorPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleOpenSignEditor(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
