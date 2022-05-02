package net.minecraft.world.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer extends SimpleContainer {
   private EnderChestBlockEntity activeChest;

   public PlayerEnderChestContainer() {
      super(27);
   }

   public void setActiveChest(EnderChestBlockEntity activeChest) {
      this.activeChest = activeChest;
   }

   public void fromTag(ListTag tag) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, ItemStack.EMPTY);
      }

      for(int var2 = 0; var2 < tag.size(); ++var2) {
         CompoundTag var3 = tag.getCompound(var2);
         int var4 = var3.getByte("Slot") & 255;
         if(var4 >= 0 && var4 < this.getContainerSize()) {
            this.setItem(var4, ItemStack.of(var3));
         }
      }

   }

   public ListTag createTag() {
      ListTag listTag = new ListTag();

      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if(!var3.isEmpty()) {
            CompoundTag var4 = new CompoundTag();
            var4.putByte("Slot", (byte)var2);
            var3.save(var4);
            listTag.add(var4);
         }
      }

      return listTag;
   }

   public boolean stillValid(Player player) {
      return this.activeChest != null && !this.activeChest.stillValid(player)?false:super.stillValid(player);
   }

   public void startOpen(Player player) {
      if(this.activeChest != null) {
         this.activeChest.startOpen();
      }

      super.startOpen(player);
   }

   public void stopOpen(Player player) {
      if(this.activeChest != null) {
         this.activeChest.stopOpen();
      }

      super.stopOpen(player);
      this.activeChest = null;
   }
}
