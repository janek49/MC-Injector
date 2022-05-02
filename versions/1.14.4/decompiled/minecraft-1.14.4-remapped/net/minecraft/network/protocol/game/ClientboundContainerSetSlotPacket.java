package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket implements Packet {
   private int containerId;
   private int slot;
   private ItemStack itemStack = ItemStack.EMPTY;

   public ClientboundContainerSetSlotPacket() {
   }

   public ClientboundContainerSetSlotPacket(int containerId, int slot, ItemStack itemStack) {
      this.containerId = containerId;
      this.slot = slot;
      this.itemStack = itemStack.copy();
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleContainerSetSlot(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.slot = friendlyByteBuf.readShort();
      this.itemStack = friendlyByteBuf.readItem();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.slot);
      friendlyByteBuf.writeItem(this.itemStack);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
