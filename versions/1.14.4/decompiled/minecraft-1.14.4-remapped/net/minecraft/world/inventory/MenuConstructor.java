package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuConstructor {
   @Nullable
   AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3);
}
