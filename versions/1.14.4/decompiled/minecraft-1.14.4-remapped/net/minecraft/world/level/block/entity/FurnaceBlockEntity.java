package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   public FurnaceBlockEntity() {
      super(BlockEntityType.FURNACE, RecipeType.SMELTING);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.furnace", new Object[0]);
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return new FurnaceMenu(var1, inventory, this, this.dataAccess);
   }
}
