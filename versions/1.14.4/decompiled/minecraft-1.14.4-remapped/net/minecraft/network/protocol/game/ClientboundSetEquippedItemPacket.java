package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquippedItemPacket implements Packet {
   private int entity;
   private EquipmentSlot slot;
   private ItemStack itemStack = ItemStack.EMPTY;

   public ClientboundSetEquippedItemPacket() {
   }

   public ClientboundSetEquippedItemPacket(int entity, EquipmentSlot slot, ItemStack itemStack) {
      this.entity = entity;
      this.slot = slot;
      this.itemStack = itemStack.copy();
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entity = friendlyByteBuf.readVarInt();
      this.slot = (EquipmentSlot)friendlyByteBuf.readEnum(EquipmentSlot.class);
      this.itemStack = friendlyByteBuf.readItem();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entity);
      friendlyByteBuf.writeEnum(this.slot);
      friendlyByteBuf.writeItem(this.itemStack);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetEquippedItem(this);
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public int getEntity() {
      return this.entity;
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }
}
