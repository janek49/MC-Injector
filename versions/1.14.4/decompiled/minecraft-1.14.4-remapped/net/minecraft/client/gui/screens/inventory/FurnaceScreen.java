package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FurnaceMenu;

@ClientJarOnly
public class FurnaceScreen extends AbstractFurnaceScreen {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

   public FurnaceScreen(FurnaceMenu furnaceMenu, Inventory inventory, Component component) {
      super(furnaceMenu, new SmeltingRecipeBookComponent(), inventory, component, TEXTURE);
   }
}
