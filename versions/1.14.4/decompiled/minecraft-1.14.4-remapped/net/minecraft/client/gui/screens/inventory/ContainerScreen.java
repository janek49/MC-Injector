package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

@ClientJarOnly
public class ContainerScreen extends AbstractContainerScreen implements MenuAccess {
   private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
   private final int containerRows;

   public ContainerScreen(ChestMenu chestMenu, Inventory inventory, Component component) {
      super(chestMenu, inventory, component);
      this.passEvents = false;
      int var4 = 222;
      int var5 = 114;
      this.containerRows = chestMenu.getRowCount();
      this.imageHeight = 114 + this.containerRows * 18;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 8.0F, 6.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
      this.blit(var4, var5 + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
   }
}
