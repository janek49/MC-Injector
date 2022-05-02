package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSetJigsawBlockPacket implements Packet {
   private BlockPos pos;
   private ResourceLocation attachementType;
   private ResourceLocation targetPool;
   private String finalState;

   public ServerboundSetJigsawBlockPacket() {
   }

   public ServerboundSetJigsawBlockPacket(BlockPos pos, ResourceLocation attachementType, ResourceLocation targetPool, String finalState) {
      this.pos = pos;
      this.attachementType = attachementType;
      this.targetPool = targetPool;
      this.finalState = finalState;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.attachementType = friendlyByteBuf.readResourceLocation();
      this.targetPool = friendlyByteBuf.readResourceLocation();
      this.finalState = friendlyByteBuf.readUtf(32767);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeResourceLocation(this.attachementType);
      friendlyByteBuf.writeResourceLocation(this.targetPool);
      friendlyByteBuf.writeUtf(this.finalState);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   public ResourceLocation getAttachementType() {
      return this.attachementType;
   }

   public String getFinalState() {
      return this.finalState;
   }
}
