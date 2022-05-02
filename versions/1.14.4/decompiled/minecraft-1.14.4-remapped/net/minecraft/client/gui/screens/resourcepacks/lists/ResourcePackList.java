package net.minecraft.client.gui.screens.resourcepacks.lists;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.resourcepacks.ResourcePackSelectScreen;
import net.minecraft.client.gui.screens.resourcepacks.lists.SelectedResourcePackList;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;

@ClientJarOnly
public abstract class ResourcePackList extends ObjectSelectionList {
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
   private static final Component INCOMPATIBLE_TITLE = new TranslatableComponent("resourcePack.incompatible", new Object[0]);
   private static final Component INCOMPATIBLE_CONFIRM_TITLE = new TranslatableComponent("resourcePack.incompatible.confirm.title", new Object[0]);
   protected final Minecraft minecraft;
   private final Component title;

   public ResourcePackList(Minecraft minecraft, int var2, int var3, Component title) {
      super(minecraft, var2, var3, 32, var3 - 55 + 4, 36);
      this.minecraft = minecraft;
      this.centerListVertically = false;
      minecraft.font.getClass();
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
      this.title = title;
   }

   protected void renderHeader(int var1, int var2, Tesselator tesselator) {
      Component var4 = (new TextComponent("")).append(this.title).withStyle(new ChatFormatting[]{ChatFormatting.UNDERLINE, ChatFormatting.BOLD});
      this.minecraft.font.draw(var4.getColoredString(), (float)(var1 + this.width / 2 - this.minecraft.font.width(var4.getColoredString()) / 2), (float)Math.min(this.y0 + 3, var2), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   public void addResourcePackEntry(ResourcePackList.ResourcePackEntry resourcePackList$ResourcePackEntry) {
      this.addEntry(resourcePackList$ResourcePackEntry);
      resourcePackList$ResourcePackEntry.parent = this;
   }

   @ClientJarOnly
   public static class ResourcePackEntry extends ObjectSelectionList.Entry {
      private ResourcePackList parent;
      protected final Minecraft minecraft;
      protected final ResourcePackSelectScreen screen;
      private final UnopenedResourcePack resourcePack;

      public ResourcePackEntry(ResourcePackList parent, ResourcePackSelectScreen screen, UnopenedResourcePack resourcePack) {
         this.screen = screen;
         this.minecraft = Minecraft.getInstance();
         this.resourcePack = resourcePack;
         this.parent = parent;
      }

      public void addToList(SelectedResourcePackList parent) {
         this.getResourcePack().getDefaultPosition().insert(parent.children(), this, ResourcePackList.ResourcePackEntry::getResourcePack, true);
         this.parent = parent;
      }

      protected void bindToIcon() {
         this.resourcePack.bindIcon(this.minecraft.getTextureManager());
      }

      protected PackCompatibility getCompatibility() {
         return this.resourcePack.getCompatibility();
      }

      protected String getDescription() {
         return this.resourcePack.getDescription().getColoredString();
      }

      protected String getName() {
         return this.resourcePack.getTitle().getColoredString();
      }

      public UnopenedResourcePack getResourcePack() {
         return this.resourcePack;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         PackCompatibility var10 = this.getCompatibility();
         if(!var10.isCompatible()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.fill(var3 - 1, var2 - 1, var3 + var4 - 9, var2 + var5 + 1, -8978432);
         }

         this.bindToIcon();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(var3, var2, 0.0F, 0.0F, 32, 32, 32, 32);
         String var11 = this.getName();
         String var12 = this.getDescription();
         if(this.showHoverOverlay() && (this.minecraft.options.touchscreen || var8)) {
            this.minecraft.getTextureManager().bind(ResourcePackList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var3, var2, var3 + 32, var2 + 32, -1601138544);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int var13 = var6 - var3;
            int var14 = var7 - var2;
            if(!var10.isCompatible()) {
               var11 = ResourcePackList.INCOMPATIBLE_TITLE.getColoredString();
               var12 = var10.getDescription().getColoredString();
            }

            if(this.canMoveRight()) {
               if(var13 < 32) {
                  GuiComponent.blit(var3, var2, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var3, var2, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if(this.canMoveLeft()) {
                  if(var13 < 16) {
                     GuiComponent.blit(var3, var2, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var3, var2, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if(this.canMoveUp()) {
                  if(var13 < 32 && var13 > 16 && var14 < 16) {
                     GuiComponent.blit(var3, var2, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var3, var2, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if(this.canMoveDown()) {
                  if(var13 < 32 && var13 > 16 && var14 > 16) {
                     GuiComponent.blit(var3, var2, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var3, var2, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         int var13 = this.minecraft.font.width(var11);
         if(var13 > 157) {
            var11 = this.minecraft.font.substrByWidth(var11, 157 - this.minecraft.font.width("...")) + "...";
         }

         this.minecraft.font.drawShadow(var11, (float)(var3 + 32 + 2), (float)(var2 + 1), 16777215);
         List<String> var14 = this.minecraft.font.split(var12, 157);

         for(int var15 = 0; var15 < 2 && var15 < var14.size(); ++var15) {
            this.minecraft.font.drawShadow((String)var14.get(var15), (float)(var3 + 32 + 2), (float)(var2 + 12 + 10 * var15), 8421504);
         }

      }

      protected boolean showHoverOverlay() {
         return !this.resourcePack.isFixedPosition() || !this.resourcePack.isRequired();
      }

      protected boolean canMoveRight() {
         return !this.screen.isSelected(this);
      }

      protected boolean canMoveLeft() {
         return this.screen.isSelected(this) && !this.resourcePack.isRequired();
      }

      protected boolean canMoveUp() {
         List<ResourcePackList.ResourcePackEntry> var1 = this.parent.children();
         int var2 = var1.indexOf(this);
         return var2 > 0 && !((ResourcePackList.ResourcePackEntry)var1.get(var2 - 1)).resourcePack.isFixedPosition();
      }

      protected boolean canMoveDown() {
         List<ResourcePackList.ResourcePackEntry> var1 = this.parent.children();
         int var2 = var1.indexOf(this);
         return var2 >= 0 && var2 < var1.size() - 1 && !((ResourcePackList.ResourcePackEntry)var1.get(var2 + 1)).resourcePack.isFixedPosition();
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         double var6 = var1 - (double)this.parent.getRowLeft();
         double var8 = var3 - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
         if(this.showHoverOverlay() && var6 <= 32.0D) {
            if(this.canMoveRight()) {
               this.getScreen().setChanged();
               PackCompatibility var10 = this.getCompatibility();
               if(var10.isCompatible()) {
                  this.getScreen().select(this);
               } else {
                  Component var11 = var10.getConfirmation();
                  this.minecraft.setScreen(new ConfirmScreen((b) -> {
                     this.minecraft.setScreen(this.getScreen());
                     if(b) {
                        this.getScreen().select(this);
                     }

                  }, ResourcePackList.INCOMPATIBLE_CONFIRM_TITLE, var11));
               }

               return true;
            }

            if(var6 < 16.0D && this.canMoveLeft()) {
               this.getScreen().deselect(this);
               return true;
            }

            if(var6 > 16.0D && var8 < 16.0D && this.canMoveUp()) {
               List<ResourcePackList.ResourcePackEntry> var10 = this.parent.children();
               int var11 = var10.indexOf(this);
               var10.remove(this);
               var10.add(var11 - 1, this);
               this.getScreen().setChanged();
               return true;
            }

            if(var6 > 16.0D && var8 > 16.0D && this.canMoveDown()) {
               List<ResourcePackList.ResourcePackEntry> var10 = this.parent.children();
               int var11 = var10.indexOf(this);
               var10.remove(this);
               var10.add(var11 + 1, this);
               this.getScreen().setChanged();
               return true;
            }
         }

         return false;
      }

      public ResourcePackSelectScreen getScreen() {
         return this.screen;
      }
   }
}
