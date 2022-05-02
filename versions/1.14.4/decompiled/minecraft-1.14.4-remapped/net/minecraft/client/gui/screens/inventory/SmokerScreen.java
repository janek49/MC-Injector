package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmokerMenu;

@ClientJarOnly
public class SmokerScreen extends AbstractFurnaceScreen {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

   public SmokerScreen(SmokerMenu smokerMenu, Inventory inventory, Component component) {
      super(smokerMenu, new SmokingRecipeBookComponent(), inventory, component, TEXTURE);
   }
}
