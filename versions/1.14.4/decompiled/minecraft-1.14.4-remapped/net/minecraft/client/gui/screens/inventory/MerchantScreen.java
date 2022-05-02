package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

@ClientJarOnly
public class MerchantScreen extends AbstractContainerScreen {
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
   private int shopItem;
   private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
   private int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu merchantMenu, Inventory inventory, Component component) {
      super(merchantMenu, inventory, component);
      this.imageWidth = 276;
   }

   private void postButtonClick() {
      ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
      ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send((Packet)(new ServerboundSelectTradePacket(this.shopItem)));
   }

   protected void init() {
      super.init();
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      int var3 = var2 + 16 + 2;

      for(int var4 = 0; var4 < 7; ++var4) {
         this.tradeOfferButtons[var4] = (MerchantScreen.TradeOfferButton)this.addButton(new MerchantScreen.TradeOfferButton(var1 + 5, var3, var4, (button) -> {
            if(button instanceof MerchantScreen.TradeOfferButton) {
               this.shopItem = ((MerchantScreen.TradeOfferButton)button).getIndex() + this.scrollOff;
               this.postButtonClick();
            }

         }));
         var3 += 20;
      }

   }

   protected void renderLabels(int var1, int var2) {
      int var3 = ((MerchantMenu)this.menu).getTraderLevel();
      int var4 = this.imageHeight - 94;
      if(var3 > 0 && var3 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
         String var5 = this.title.getColoredString();
         String var6 = "- " + I18n.get("merchant.level." + var3, new Object[0]);
         int var7 = this.font.width(var5);
         int var8 = this.font.width(var6);
         int var9 = var7 + var8 + 3;
         int var10 = 49 + this.imageWidth / 2 - var9 / 2;
         this.font.draw(var5, (float)var10, 6.0F, 4210752);
         this.font.draw(this.inventory.getDisplayName().getColoredString(), 107.0F, (float)var4, 4210752);
         this.font.draw(var6, (float)(var10 + var7 + 3), 6.0F, 4210752);
      } else {
         String var5 = this.title.getColoredString();
         this.font.draw(var5, (float)(49 + this.imageWidth / 2 - this.font.width(var5) / 2), 6.0F, 4210752);
         this.font.draw(this.inventory.getDisplayName().getColoredString(), 107.0F, (float)var4, 4210752);
      }

      String var5 = I18n.get("merchant.trades", new Object[0]);
      int var6 = this.font.width(var5);
      this.font.draw(var5, (float)(5 - var6 / 2 + 48), 6.0F, 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      blit(var4, var5, this.blitOffset, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 512);
      MerchantOffers var6 = ((MerchantMenu)this.menu).getOffers();
      if(!var6.isEmpty()) {
         int var7 = this.shopItem;
         if(var7 < 0 || var7 >= var6.size()) {
            return;
         }

         MerchantOffer var8 = (MerchantOffer)var6.get(var7);
         if(var8.isOutOfStock()) {
            this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            blit(this.leftPos + 83 + 99, this.topPos + 35, this.blitOffset, 311.0F, 0.0F, 28, 21, 256, 512);
         }
      }

   }

   private void renderProgressBar(int var1, int var2, MerchantOffer merchantOffer) {
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int var4 = ((MerchantMenu)this.menu).getTraderLevel();
      int var5 = ((MerchantMenu)this.menu).getTraderXp();
      if(var4 < 5) {
         blit(var1 + 136, var2 + 16, this.blitOffset, 0.0F, 186.0F, 102, 5, 256, 512);
         int var6 = VillagerData.getMinXpPerLevel(var4);
         if(var5 >= var6 && VillagerData.canLevelUp(var4)) {
            int var7 = 100;
            float var8 = (float)(100 / (VillagerData.getMaxXpPerLevel(var4) - var6));
            int var9 = Mth.floor(var8 * (float)(var5 - var6));
            blit(var1 + 136, var2 + 16, this.blitOffset, 0.0F, 191.0F, var9 + 1, 5, 256, 512);
            int var10 = ((MerchantMenu)this.menu).getFutureTraderXp();
            if(var10 > 0) {
               int var11 = Math.min(Mth.floor((float)var10 * var8), 100 - var9);
               blit(var1 + 136 + var9 + 1, var2 + 16 + 1, this.blitOffset, 2.0F, 182.0F, var11, 3, 256, 512);
            }

         }
      }
   }

   private void renderScroller(int var1, int var2, MerchantOffers merchantOffers) {
      Lighting.turnOff();
      int var4 = merchantOffers.size() + 1 - 7;
      if(var4 > 1) {
         int var5 = 139 - (27 + (var4 - 1) * 139 / var4);
         int var6 = 1 + var5 / var4 + 139 / var4;
         int var7 = 113;
         int var8 = Math.min(113, this.scrollOff * var6);
         if(this.scrollOff == var4 - 1) {
            var8 = 113;
         }

         blit(var1 + 94, var2 + 18 + var8, this.blitOffset, 0.0F, 199.0F, 6, 27, 256, 512);
      } else {
         blit(var1 + 94, var2 + 18, this.blitOffset, 6.0F, 199.0F, 6, 27, 256, 512);
      }

      Lighting.turnOnGui();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      MerchantOffers var4 = ((MerchantMenu)this.menu).getOffers();
      if(!var4.isEmpty()) {
         int var5 = (this.width - this.imageWidth) / 2;
         int var6 = (this.height - this.imageHeight) / 2;
         int var7 = var6 + 16 + 1;
         int var8 = var5 + 5 + 5;
         GlStateManager.pushMatrix();
         Lighting.turnOnGui();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.renderScroller(var5, var6, var4);
         int var9 = 0;

         for(MerchantOffer var11 : var4) {
            if(this.canScroll(var4.size()) && (var9 < this.scrollOff || var9 >= 7 + this.scrollOff)) {
               ++var9;
            } else {
               ItemStack var12 = var11.getBaseCostA();
               ItemStack var13 = var11.getCostA();
               ItemStack var14 = var11.getCostB();
               ItemStack var15 = var11.getResult();
               this.itemRenderer.blitOffset = 100.0F;
               int var16 = var7 + 2;
               this.renderAndDecorateCostA(var13, var12, var8, var16);
               if(!var14.isEmpty()) {
                  this.itemRenderer.renderAndDecorateItem(var14, var5 + 5 + 35, var16);
                  this.itemRenderer.renderGuiItemDecorations(this.font, var14, var5 + 5 + 35, var16);
               }

               this.renderButtonArrows(var11, var5, var16);
               this.itemRenderer.renderAndDecorateItem(var15, var5 + 5 + 68, var16);
               this.itemRenderer.renderGuiItemDecorations(this.font, var15, var5 + 5 + 68, var16);
               this.itemRenderer.blitOffset = 0.0F;
               var7 += 20;
               ++var9;
            }
         }

         int var10 = this.shopItem;
         MerchantOffer var11 = (MerchantOffer)var4.get(var10);
         GlStateManager.disableLighting();
         if(((MerchantMenu)this.menu).showProgressBar()) {
            this.renderProgressBar(var5, var6, var11);
         }

         if(var11.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var1, (double)var2) && ((MerchantMenu)this.menu).canRestock()) {
            this.renderTooltip(I18n.get("merchant.deprecated", new Object[0]), var1, var2);
         }

         for(MerchantScreen.TradeOfferButton var15 : this.tradeOfferButtons) {
            if(var15.isHovered()) {
               var15.renderToolTip(var1, var2);
            }

            var15.visible = var15.index < ((MerchantMenu)this.menu).getOffers().size();
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         Lighting.turnOn();
      }

      this.renderTooltip(var1, var2);
   }

   private void renderButtonArrows(MerchantOffer merchantOffer, int var2, int var3) {
      Lighting.turnOff();
      GlStateManager.enableBlend();
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      if(merchantOffer.isOutOfStock()) {
         blit(var2 + 5 + 35 + 20, var3 + 3, this.blitOffset, 25.0F, 171.0F, 10, 9, 256, 512);
      } else {
         blit(var2 + 5 + 35 + 20, var3 + 3, this.blitOffset, 15.0F, 171.0F, 10, 9, 256, 512);
      }

      Lighting.turnOnGui();
   }

   private void renderAndDecorateCostA(ItemStack var1, ItemStack var2, int var3, int var4) {
      this.itemRenderer.renderAndDecorateItem(var1, var3, var4);
      if(var2.getCount() == var1.getCount()) {
         this.itemRenderer.renderGuiItemDecorations(this.font, var1, var3, var4);
      } else {
         this.itemRenderer.renderGuiItemDecorations(this.font, var2, var3, var4, var2.getCount() == 1?"1":null);
         this.itemRenderer.renderGuiItemDecorations(this.font, var1, var3 + 14, var4, var1.getCount() == 1?"1":null);
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.blitOffset += 300;
         Lighting.turnOff();
         blit(var3 + 7, var4 + 12, this.blitOffset, 0.0F, 176.0F, 9, 2, 256, 512);
         Lighting.turnOnGui();
         this.blitOffset -= 300;
      }

   }

   private boolean canScroll(int i) {
      return i > 7;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      int var7 = ((MerchantMenu)this.menu).getOffers().size();
      if(this.canScroll(var7)) {
         int var8 = var7 - 7;
         this.scrollOff = (int)((double)this.scrollOff - var5);
         this.scrollOff = Mth.clamp(this.scrollOff, 0, var8);
      }

      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      int var10 = ((MerchantMenu)this.menu).getOffers().size();
      if(this.isDragging) {
         int var11 = this.topPos + 18;
         int var12 = var11 + 139;
         int var13 = var10 - 7;
         float var14 = ((float)var3 - (float)var11 - 13.5F) / ((float)(var12 - var11) - 27.0F);
         var14 = var14 * (float)var13 + 0.5F;
         this.scrollOff = Mth.clamp((int)var14, 0, var13);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.isDragging = false;
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;
      if(this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && var1 > (double)(var6 + 94) && var1 < (double)(var6 + 94 + 6) && var3 > (double)(var7 + 18) && var3 <= (double)(var7 + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @ClientJarOnly
   class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(int var2, int var3, int index, Button.OnPress button$OnPress) {
         super(var2, var3, 89, 20, "", button$OnPress);
         this.index = index;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(int var1, int var2) {
         if(this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if(var1 < this.x + 20) {
               ItemStack var3 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
               MerchantScreen.this.renderTooltip(var3, var1, var2);
            } else if(var1 < this.x + 50 && var1 > this.x + 30) {
               ItemStack var3 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
               if(!var3.isEmpty()) {
                  MerchantScreen.this.renderTooltip(var3, var1, var2);
               }
            } else if(var1 > this.x + 65) {
               ItemStack var3 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
               MerchantScreen.this.renderTooltip(var3, var1, var2);
            }
         }

      }
   }
}
