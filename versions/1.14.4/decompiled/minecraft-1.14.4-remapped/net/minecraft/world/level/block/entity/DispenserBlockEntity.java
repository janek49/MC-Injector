package net.minecraft.world.level.block.entity;

import java.util.Random;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
   private static final Random RANDOM = new Random();
   private NonNullList items;

   protected DispenserBlockEntity(BlockEntityType blockEntityType) {
      super(blockEntityType);
      this.items = NonNullList.withSize(9, ItemStack.EMPTY);
   }

   public DispenserBlockEntity() {
      this(BlockEntityType.DISPENSER);
   }

   public int getContainerSize() {
      return 9;
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int getRandomSlot() {
      this.unpackLootTable((Player)null);
      int var1 = -1;
      int var2 = 1;

      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         if(!((ItemStack)this.items.get(var3)).isEmpty() && RANDOM.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   public int addItem(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if(((ItemStack)this.items.get(var2)).isEmpty()) {
            this.setItem(var2, itemStack);
            return var2;
         }
      }

      return -1;
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.dispenser", new Object[0]);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if(!this.tryLoadLootTable(compoundTag)) {
         ContainerHelper.loadAllItems(compoundTag, this.items);
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(!this.trySaveLootTable(compoundTag)) {
         ContainerHelper.saveAllItems(compoundTag, this.items);
      }

      return compoundTag;
   }

   protected NonNullList getItems() {
      return this.items;
   }

   protected void setItems(NonNullList items) {
      this.items = items;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return new DispenserMenu(var1, inventory, this);
   }
}
