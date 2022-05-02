package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;

public class FurnaceMenu extends AbstractFurnaceMenu {
   public FurnaceMenu(int var1, Inventory inventory) {
      super(MenuType.FURNACE, RecipeType.SMELTING, var1, inventory);
   }

   public FurnaceMenu(int var1, Inventory inventory, Container container, ContainerData containerData) {
      super(MenuType.FURNACE, RecipeType.SMELTING, var1, inventory, container, containerData);
   }
}
