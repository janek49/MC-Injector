package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket implements Packet {
   private int slotNum;
   private ItemStack itemStack = ItemStack.EMPTY;

   public ServerboundSetCreativeModeSlotPacket() {
   }

   public ServerboundSetCreativeModeSlotPacket(int slotNum, ItemStack itemStack) {
      this.slotNum = slotNum;
      this.itemStack = itemStack.copy();
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetCreativeModeSlot(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.slotNum = friendlyByteBuf.readShort();
      this.itemStack = friendlyByteBuf.readItem();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeShort(this.slotNum);
      friendlyByteBuf.writeItem(this.itemStack);
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
