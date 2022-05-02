package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
   public SmokerBlockEntity() {
      super(BlockEntityType.SMOKER, RecipeType.SMOKING);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.smoker", new Object[0]);
   }

   protected int getBurnDuration(ItemStack itemStack) {
      return super.getBurnDuration(itemStack) / 2;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return new SmokerMenu(var1, inventory, this, this.dataAccess);
   }
}
