package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundLevelEventPacket implements Packet {
   private int type;
   private BlockPos pos;
   private int data;
   private boolean globalEvent;

   public ClientboundLevelEventPacket() {
   }

   public ClientboundLevelEventPacket(int type, BlockPos blockPos, int data, boolean globalEvent) {
      this.type = type;
      this.pos = blockPos.immutable();
      this.data = data;
      this.globalEvent = globalEvent;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.type = friendlyByteBuf.readInt();
      this.pos = friendlyByteBuf.readBlockPos();
      this.data = friendlyByteBuf.readInt();
      this.globalEvent = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.type);
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeInt(this.data);
      friendlyByteBuf.writeBoolean(this.globalEvent);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleLevelEvent(this);
   }

   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
