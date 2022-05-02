package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.inventory.AbstractContainerMenu;

@ClientJarOnly
public interface MenuAccess {
   AbstractContainerMenu getMenu();
}
