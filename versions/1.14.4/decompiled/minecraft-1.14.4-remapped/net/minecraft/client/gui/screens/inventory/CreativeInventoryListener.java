package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class CreativeInventoryListener implements ContainerListener {
   private final Minecraft minecraft;

   public CreativeInventoryListener(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList nonNullList) {
   }

   public void slotChanged(AbstractContainerMenu abstractContainerMenu, int var2, ItemStack itemStack) {
      this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, var2);
   }

   public void setContainerData(AbstractContainerMenu abstractContainerMenu, int var2, int var3) {
   }
}
