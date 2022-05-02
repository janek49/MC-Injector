package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.TagManager;

public class ClientboundUpdateTagsPacket implements Packet {
   private TagManager tags;

   public ClientboundUpdateTagsPacket() {
   }

   public ClientboundUpdateTagsPacket(TagManager tags) {
      this.tags = tags;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.tags = TagManager.deserializeFromNetwork(friendlyByteBuf);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.tags.serializeToNetwork(friendlyByteBuf);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleUpdateTags(this);
   }

   public TagManager getTags() {
      return this.tags;
   }
}
