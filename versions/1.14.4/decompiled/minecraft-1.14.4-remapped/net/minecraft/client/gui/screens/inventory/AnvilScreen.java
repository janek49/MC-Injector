package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class AnvilScreen extends AbstractContainerScreen implements ContainerListener {
   private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
   private EditBox name;

   public AnvilScreen(AnvilMenu anvilMenu, Inventory inventory, Component component) {
      super(anvilMenu, inventory, component);
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      this.name = new EditBox(this.font, var1 + 62, var2 + 24, 103, 12, I18n.get("container.repair", new Object[0]));
      this.name.setCanLoseFocus(false);
      this.name.changeFocus(true);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(35);
      this.name.setResponder(this::onNameChanged);
      this.children.add(this.name);
      ((AnvilMenu)this.menu).addSlotListener(this);
      this.setInitialFocus(this.name);
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      String var4 = this.name.getValue();
      this.init(minecraft, var2, var3);
      this.name.setValue(var4);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      ((AnvilMenu)this.menu).removeSlotListener(this);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(var1, var2, var3) && !this.name.canConsumeInput()?super.keyPressed(var1, var2, var3):true;
   }

   protected void renderLabels(int var1, int var2) {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.font.draw(this.title.getColoredString(), 60.0F, 6.0F, 4210752);
      int var3 = ((AnvilMenu)this.menu).getCost();
      if(var3 > 0) {
         int var4 = 8453920;
         boolean var5 = true;
         String var6 = I18n.get("container.repair.cost", new Object[]{Integer.valueOf(var3)});
         if(var3 >= 40 && !this.minecraft.player.abilities.instabuild) {
            var6 = I18n.get("container.repair.expensive", new Object[0]);
            var4 = 16736352;
         } else if(!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
            var5 = false;
         } else if(!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.inventory.player)) {
            var4 = 16736352;
         }

         if(var5) {
            int var7 = this.imageWidth - 8 - this.font.width(var6) - 2;
            int var8 = 69;
            fill(var7 - 2, 67, this.imageWidth - 8, 79, 1325400064);
            this.font.drawShadow(var6, (float)var7, 69.0F, var4);
         }
      }

      GlStateManager.enableLighting();
   }

   private void onNameChanged(String string) {
      if(!string.isEmpty()) {
         String string = string;
         Slot var3 = ((AnvilMenu)this.menu).getSlot(0);
         if(var3 != null && var3.hasItem() && !var3.getItem().hasCustomHoverName() && string.equals(var3.getItem().getHoverName().getString())) {
            string = "";
         }

         ((AnvilMenu)this.menu).setItemName(string);
         this.minecraft.player.connection.send((Packet)(new ServerboundRenameItemPacket(string)));
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.name.render(var1, var2, var3);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ANVIL_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(var4 + 59, var5 + 20, 0, this.imageHeight + (((AnvilMenu)this.menu).getSlot(0).hasItem()?0:16), 110, 16);
      if((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(2).hasItem()) {
         this.blit(var4 + 99, var5 + 45, this.imageWidth, 0, 28, 21);
      }

   }

   public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList nonNullList) {
      this.slotChanged(abstractContainerMenu, 0, abstractContainerMenu.getSlot(0).getItem());
   }

   public void slotChanged(AbstractContainerMenu abstractContainerMenu, int var2, ItemStack itemStack) {
      if(var2 == 0) {
         this.name.setValue(itemStack.isEmpty()?"":itemStack.getHoverName().getString());
         this.name.setEditable(!itemStack.isEmpty());
      }

   }

   public void setContainerData(AbstractContainerMenu abstractContainerMenu, int var2, int var3) {
   }
}
