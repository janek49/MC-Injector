package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket implements Packet {
   private int containerId;
   private List items;

   public ClientboundContainerSetContentPacket() {
   }

   public ClientboundContainerSetContentPacket(int containerId, NonNullList nonNullList) {
      this.containerId = containerId;
      this.items = NonNullList.withSize(nonNullList.size(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         this.items.set(var3, ((ItemStack)nonNullList.get(var3)).copy());
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readUnsignedByte();
      int var2 = friendlyByteBuf.readShort();
      this.items = NonNullList.withSize(var2, ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.items.set(var3, friendlyByteBuf.readItem());
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.items.size());

      for(ItemStack var3 : this.items) {
         friendlyByteBuf.writeItem(var3);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleContainerContent(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public List getItems() {
      return this.items;
   }
}
