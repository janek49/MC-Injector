package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet {
   private int containerId;
   private int slotNum;
   private int buttonNum;
   private short uid;
   private ItemStack itemStack = ItemStack.EMPTY;
   private ClickType clickType;

   public ServerboundContainerClickPacket() {
   }

   public ServerboundContainerClickPacket(int containerId, int slotNum, int buttonNum, ClickType clickType, ItemStack itemStack, short uid) {
      this.containerId = containerId;
      this.slotNum = slotNum;
      this.buttonNum = buttonNum;
      this.itemStack = itemStack.copy();
      this.uid = uid;
      this.clickType = clickType;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleContainerClick(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.slotNum = friendlyByteBuf.readShort();
      this.buttonNum = friendlyByteBuf.readByte();
      this.uid = friendlyByteBuf.readShort();
      this.clickType = (ClickType)friendlyByteBuf.readEnum(ClickType.class);
      this.itemStack = friendlyByteBuf.readItem();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeShort(this.slotNum);
      friendlyByteBuf.writeByte(this.buttonNum);
      friendlyByteBuf.writeShort(this.uid);
      friendlyByteBuf.writeEnum(this.clickType);
      friendlyByteBuf.writeItem(this.itemStack);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public int getButtonNum() {
      return this.buttonNum;
   }

   public short getUid() {
      return this.uid;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public ClickType getClickType() {
      return this.clickType;
   }
}
