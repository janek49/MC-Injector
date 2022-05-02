package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSignUpdatePacket implements Packet {
   private BlockPos pos;
   private String[] lines;

   public ServerboundSignUpdatePacket() {
   }

   public ServerboundSignUpdatePacket(BlockPos pos, Component var2, Component var3, Component var4, Component var5) {
      this.pos = pos;
      this.lines = new String[]{var2.getString(), var3.getString(), var4.getString(), var5.getString()};
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.lines = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.lines[var2] = friendlyByteBuf.readUtf(384);
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);

      for(int var2 = 0; var2 < 4; ++var2) {
         friendlyByteBuf.writeUtf(this.lines[var2]);
      }

   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}
