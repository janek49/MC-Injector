package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundTagQueryPacket implements Packet {
   private int transactionId;
   @Nullable
   private CompoundTag tag;

   public ClientboundTagQueryPacket() {
   }

   public ClientboundTagQueryPacket(int transactionId, @Nullable CompoundTag tag) {
      this.transactionId = transactionId;
      this.tag = tag;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.transactionId = friendlyByteBuf.readVarInt();
      this.tag = friendlyByteBuf.readNbt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.transactionId);
      friendlyByteBuf.writeNbt(this.tag);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleTagQueryPacket(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public boolean isSkippable() {
      return true;
   }
}
