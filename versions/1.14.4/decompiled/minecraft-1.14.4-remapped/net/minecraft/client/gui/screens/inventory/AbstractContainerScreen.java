package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public abstract class AbstractContainerScreen extends Screen implements MenuAccess {
   public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   protected final AbstractContainerMenu menu;
   protected final Inventory inventory;
   protected int leftPos;
   protected int topPos;
   protected Slot hoveredSlot;
   private Slot clickedSlot;
   private boolean isSplittingStack;
   private ItemStack draggingItem = ItemStack.EMPTY;
   private int snapbackStartX;
   private int snapbackStartY;
   private Slot snapbackEnd;
   private long snapbackTime;
   private ItemStack snapbackItem = ItemStack.EMPTY;
   private Slot quickdropSlot;
   private long quickdropTime;
   protected final Set quickCraftSlots = Sets.newHashSet();
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private long lastClickTime;
   private Slot lastClickSlot;
   private int lastClickButton;
   private boolean doubleclick;
   private ItemStack lastQuickMoved = ItemStack.EMPTY;

   public AbstractContainerScreen(AbstractContainerMenu menu, Inventory inventory, Component component) {
      super(component);
      this.menu = menu;
      this.inventory = inventory;
      this.skipNextRelease = true;
   }

   protected void init() {
      super.init();
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
   }

   public void render(int var1, int var2, float var3) {
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.renderBg(var3, var1, var2);
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
      GlStateManager.disableLighting();
      GlStateManager.disableDepthTest();
      super.render(var1, var2, var3);
      Lighting.turnOnGui();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var4, (float)var5, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.hoveredSlot = null;
      int var6 = 240;
      int var7 = 240;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      for(int var8 = 0; var8 < this.menu.slots.size(); ++var8) {
         Slot var9 = (Slot)this.menu.slots.get(var8);
         if(var9.isActive()) {
            this.renderSlot(var9);
         }

         if(this.isHovering(var9, (double)var1, (double)var2) && var9.isActive()) {
            this.hoveredSlot = var9;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int var10 = var9.x;
            int var11 = var9.y;
            GlStateManager.colorMask(true, true, true, false);
            this.fillGradient(var10, var11, var10 + 16, var11 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }
      }

      Lighting.turnOff();
      this.renderLabels(var1, var2);
      Lighting.turnOnGui();
      Inventory var8 = this.minecraft.player.inventory;
      ItemStack var9 = this.draggingItem.isEmpty()?var8.getCarried():this.draggingItem;
      if(!var9.isEmpty()) {
         int var10 = 8;
         int var11 = this.draggingItem.isEmpty()?8:16;
         String var12 = null;
         if(!this.draggingItem.isEmpty() && this.isSplittingStack) {
            var9 = var9.copy();
            var9.setCount(Mth.ceil((float)var9.getCount() / 2.0F));
         } else if(this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            var9 = var9.copy();
            var9.setCount(this.quickCraftingRemainder);
            if(var9.isEmpty()) {
               var12 = "" + ChatFormatting.YELLOW + "0";
            }
         }

         this.renderFloatingItem(var9, var1 - var4 - 8, var2 - var5 - var11, var12);
      }

      if(!this.snapbackItem.isEmpty()) {
         float var10 = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
         if(var10 >= 1.0F) {
            var10 = 1.0F;
            this.snapbackItem = ItemStack.EMPTY;
         }

         int var11 = this.snapbackEnd.x - this.snapbackStartX;
         int var12 = this.snapbackEnd.y - this.snapbackStartY;
         int var13 = this.snapbackStartX + (int)((float)var11 * var10);
         int var14 = this.snapbackStartY + (int)((float)var12 * var10);
         this.renderFloatingItem(this.snapbackItem, var13, var14, (String)null);
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableDepthTest();
      Lighting.turnOn();
   }

   protected void renderTooltip(int var1, int var2) {
      if(this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         this.renderTooltip(this.hoveredSlot.getItem(), var1, var2);
      }

   }

   private void renderFloatingItem(ItemStack itemStack, int var2, int var3, String string) {
      GlStateManager.translatef(0.0F, 0.0F, 32.0F);
      this.blitOffset = 200;
      this.itemRenderer.blitOffset = 200.0F;
      this.itemRenderer.renderAndDecorateItem(itemStack, var2, var3);
      this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, var2, var3 - (this.draggingItem.isEmpty()?0:8), string);
      this.blitOffset = 0;
      this.itemRenderer.blitOffset = 0.0F;
   }

   protected void renderLabels(int var1, int var2) {
   }

   protected abstract void renderBg(float var1, int var2, int var3);

   private void renderSlot(Slot slot) {
      int var2 = slot.x;
      int var3 = slot.y;
      ItemStack var4 = slot.getItem();
      boolean var5 = false;
      boolean var6 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack var7 = this.minecraft.player.inventory.getCarried();
      String var8 = null;
      if(slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !var4.isEmpty()) {
         var4 = var4.copy();
         var4.setCount(var4.getCount() / 2);
      } else if(this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !var7.isEmpty()) {
         if(this.quickCraftSlots.size() == 1) {
            return;
         }

         if(AbstractContainerMenu.canItemQuickReplace(slot, var7, true) && this.menu.canDragTo(slot)) {
            var4 = var7.copy();
            var5 = true;
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, var4, slot.getItem().isEmpty()?0:slot.getItem().getCount());
            int var9 = Math.min(var4.getMaxStackSize(), slot.getMaxStackSize(var4));
            if(var4.getCount() > var9) {
               var8 = ChatFormatting.YELLOW.toString() + var9;
               var4.setCount(var9);
            }
         } else {
            this.quickCraftSlots.remove(slot);
            this.recalculateQuickCraftRemaining();
         }
      }

      this.blitOffset = 100;
      this.itemRenderer.blitOffset = 100.0F;
      if(var4.isEmpty() && slot.isActive()) {
         String var9 = slot.getNoItemIcon();
         if(var9 != null) {
            TextureAtlasSprite var10 = this.minecraft.getTextureAtlas().getTexture(var9);
            GlStateManager.disableLighting();
            this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
            blit(var2, var3, this.blitOffset, 16, 16, var10);
            GlStateManager.enableLighting();
            var6 = true;
         }
      }

      if(!var6) {
         if(var5) {
            fill(var2, var3, var2 + 16, var3 + 16, -2130706433);
         }

         GlStateManager.enableDepthTest();
         this.itemRenderer.renderAndDecorateItem(this.minecraft.player, var4, var2, var3);
         this.itemRenderer.renderGuiItemDecorations(this.font, var4, var2, var3, var8);
      }

      this.itemRenderer.blitOffset = 0.0F;
      this.blitOffset = 0;
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack var1 = this.minecraft.player.inventory.getCarried();
      if(!var1.isEmpty() && this.isQuickCrafting) {
         if(this.quickCraftingType == 2) {
            this.quickCraftingRemainder = var1.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = var1.getCount();

            for(Slot var3 : this.quickCraftSlots) {
               ItemStack var4 = var1.copy();
               ItemStack var5 = var3.getItem();
               int var6 = var5.isEmpty()?0:var5.getCount();
               AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, var4, var6);
               int var7 = Math.min(var4.getMaxStackSize(), var3.getMaxStackSize(var4));
               if(var4.getCount() > var7) {
                  var4.setCount(var7);
               }

               this.quickCraftingRemainder -= var4.getCount() - var6;
            }

         }
      }
   }

   private Slot findSlot(double var1, double var3) {
      for(int var5 = 0; var5 < this.menu.slots.size(); ++var5) {
         Slot var6 = (Slot)this.menu.slots.get(var5);
         if(this.isHovering(var6, var1, var3) && var6.isActive()) {
            return var6;
         }
      }

      return null;
   }

   public boolean mouseClicked(double var1, double var3, int quickCraftingButton) {
      if(super.mouseClicked(var1, var3, quickCraftingButton)) {
         return true;
      } else {
         boolean var6 = this.minecraft.options.keyPickItem.matchesMouse(quickCraftingButton);
         Slot var7 = this.findSlot(var1, var3);
         long var8 = Util.getMillis();
         this.doubleclick = this.lastClickSlot == var7 && var8 - this.lastClickTime < 250L && this.lastClickButton == quickCraftingButton;
         this.skipNextRelease = false;
         if(quickCraftingButton == 0 || quickCraftingButton == 1 || var6) {
            int var10 = this.leftPos;
            int var11 = this.topPos;
            boolean var12 = this.hasClickedOutside(var1, var3, var10, var11, quickCraftingButton);
            int var13 = -1;
            if(var7 != null) {
               var13 = var7.index;
            }

            if(var12) {
               var13 = -999;
            }

            if(this.minecraft.options.touchscreen && var12 && this.minecraft.player.inventory.getCarried().isEmpty()) {
               this.minecraft.setScreen((Screen)null);
               return true;
            }

            if(var13 != -1) {
               if(this.minecraft.options.touchscreen) {
                  if(var7 != null && var7.hasItem()) {
                     this.clickedSlot = var7;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = quickCraftingButton == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if(!this.isQuickCrafting) {
                  if(this.minecraft.player.inventory.getCarried().isEmpty()) {
                     if(this.minecraft.options.keyPickItem.matchesMouse(quickCraftingButton)) {
                        this.slotClicked(var7, var13, quickCraftingButton, ClickType.CLONE);
                     } else {
                        boolean var14 = var13 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
                        ClickType var15 = ClickType.PICKUP;
                        if(var14) {
                           this.lastQuickMoved = var7 != null && var7.hasItem()?var7.getItem().copy():ItemStack.EMPTY;
                           var15 = ClickType.QUICK_MOVE;
                        } else if(var13 == -999) {
                           var15 = ClickType.THROW;
                        }

                        this.slotClicked(var7, var13, quickCraftingButton, var15);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = quickCraftingButton;
                     this.quickCraftSlots.clear();
                     if(quickCraftingButton == 0) {
                        this.quickCraftingType = 0;
                     } else if(quickCraftingButton == 1) {
                        this.quickCraftingType = 1;
                     } else if(this.minecraft.options.keyPickItem.matchesMouse(quickCraftingButton)) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = var7;
         this.lastClickTime = var8;
         this.lastClickButton = quickCraftingButton;
         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      Slot var10 = this.findSlot(var1, var3);
      ItemStack var11 = this.minecraft.player.inventory.getCarried();
      if(this.clickedSlot != null && this.minecraft.options.touchscreen) {
         if(var5 == 0 || var5 == 1) {
            if(this.draggingItem.isEmpty()) {
               if(var10 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if(this.draggingItem.getCount() > 1 && var10 != null && AbstractContainerMenu.canItemQuickReplace(var10, this.draggingItem, false)) {
               long var12 = Util.getMillis();
               if(this.quickdropSlot == var10) {
                  if(var12 - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.slotClicked(var10, var10.index, 1, ClickType.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.quickdropTime = var12 + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = var10;
                  this.quickdropTime = var12;
               }
            }
         }
      } else if(this.isQuickCrafting && var10 != null && !var11.isEmpty() && (var11.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(var10, var11, true) && var10.mayPlace(var11) && this.menu.canDragTo(var10)) {
         this.quickCraftSlots.add(var10);
         this.recalculateQuickCraftRemaining();
      }

      return true;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      Slot var6 = this.findSlot(var1, var3);
      int var7 = this.leftPos;
      int var8 = this.topPos;
      boolean var9 = this.hasClickedOutside(var1, var3, var7, var8, var5);
      int var10 = -1;
      if(var6 != null) {
         var10 = var6.index;
      }

      if(var9) {
         var10 = -999;
      }

      if(this.doubleclick && var6 != null && var5 == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, var6)) {
         if(hasShiftDown()) {
            if(!this.lastQuickMoved.isEmpty()) {
               for(Slot var12 : this.menu.slots) {
                  if(var12 != null && var12.mayPickup(this.minecraft.player) && var12.hasItem() && var12.container == var6.container && AbstractContainerMenu.canItemQuickReplace(var12, this.lastQuickMoved, true)) {
                     this.slotClicked(var12, var12.index, var5, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(var6, var10, var5, ClickType.PICKUP_ALL);
         }

         this.doubleclick = false;
         this.lastClickTime = 0L;
      } else {
         if(this.isQuickCrafting && this.quickCraftingButton != var5) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if(this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         if(this.clickedSlot != null && this.minecraft.options.touchscreen) {
            if(var5 == 0 || var5 == 1) {
               if(this.draggingItem.isEmpty() && var6 != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               boolean var11 = AbstractContainerMenu.canItemQuickReplace(var6, this.draggingItem, false);
               if(var10 != -1 && !this.draggingItem.isEmpty() && var11) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, var5, ClickType.PICKUP);
                  this.slotClicked(var6, var10, 0, ClickType.PICKUP);
                  if(this.minecraft.player.inventory.getCarried().isEmpty()) {
                     this.snapbackItem = ItemStack.EMPTY;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, var5, ClickType.PICKUP);
                     this.snapbackStartX = Mth.floor(var1 - (double)var7);
                     this.snapbackStartY = Mth.floor(var3 - (double)var8);
                     this.snapbackEnd = this.clickedSlot;
                     this.snapbackItem = this.draggingItem;
                     this.snapbackTime = Util.getMillis();
                  }
               } else if(!this.draggingItem.isEmpty()) {
                  this.snapbackStartX = Mth.floor(var1 - (double)var7);
                  this.snapbackStartY = Mth.floor(var3 - (double)var8);
                  this.snapbackEnd = this.clickedSlot;
                  this.snapbackItem = this.draggingItem;
                  this.snapbackTime = Util.getMillis();
               }

               this.draggingItem = ItemStack.EMPTY;
               this.clickedSlot = null;
            }
         } else if(this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

            for(Slot var12 : this.quickCraftSlots) {
               this.slotClicked(var12, var12.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if(!this.minecraft.player.inventory.getCarried().isEmpty()) {
            if(this.minecraft.options.keyPickItem.matchesMouse(var5)) {
               this.slotClicked(var6, var10, var5, ClickType.CLONE);
            } else {
               boolean var11 = var10 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
               if(var11) {
                  this.lastQuickMoved = var6 != null && var6.hasItem()?var6.getItem().copy():ItemStack.EMPTY;
               }

               this.slotClicked(var6, var10, var5, var11?ClickType.QUICK_MOVE:ClickType.PICKUP);
            }
         }
      }

      if(this.minecraft.player.inventory.getCarried().isEmpty()) {
         this.lastClickTime = 0L;
      }

      this.isQuickCrafting = false;
      return true;
   }

   private boolean isHovering(Slot slot, double var2, double var4) {
      return this.isHovering(slot.x, slot.y, 16, 16, var2, var4);
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      int var9 = this.leftPos;
      int var10 = this.topPos;
      var5 = var5 - (double)var9;
      var7 = var7 - (double)var10;
      return var5 >= (double)(var1 - 1) && var5 < (double)(var1 + var3 + 1) && var7 >= (double)(var2 - 1) && var7 < (double)(var2 + var4 + 1);
   }

   protected void slotClicked(Slot slot, int var2, int var3, ClickType clickType) {
      if(slot != null) {
         var2 = slot.index;
      }

      this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, var2, var3, clickType, this.minecraft.player);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if(var1 == 256 || this.minecraft.options.keyInventory.matches(var1, var2)) {
            this.minecraft.player.closeContainer();
         }

         this.checkNumkeyPressed(var1, var2);
         if(this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if(this.minecraft.options.keyPickItem.matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if(this.minecraft.options.keyDrop.matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown()?1:0, ClickType.THROW);
            }
         }

         return true;
      }
   }

   protected boolean checkNumkeyPressed(int var1, int var2) {
      if(this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null) {
         for(int var3 = 0; var3 < 9; ++var3) {
            if(this.minecraft.options.keyHotbarSlots[var3].matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, var3, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void removed() {
      if(this.minecraft.player != null) {
         this.menu.removed(this.minecraft.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      if(!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
         this.minecraft.player.closeContainer();
      }

   }

   public AbstractContainerMenu getMenu() {
      return this.menu;
   }
}
