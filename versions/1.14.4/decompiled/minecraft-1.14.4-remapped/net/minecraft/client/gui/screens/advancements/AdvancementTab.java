package net.minecraft.client.gui.screens.advancements;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class AdvancementTab extends GuiComponent {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final String title;
   private final AdvancementWidget root;
   private final Map widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = Integer.MAX_VALUE;
   private int minY = Integer.MAX_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private int maxY = Integer.MIN_VALUE;
   private float fade;
   private boolean centered;

   public AdvancementTab(Minecraft minecraft, AdvancementsScreen screen, AdvancementTabType type, int index, Advancement advancement, DisplayInfo display) {
      this.minecraft = minecraft;
      this.screen = screen;
      this.type = type;
      this.index = index;
      this.advancement = advancement;
      this.display = display;
      this.icon = display.getIcon();
      this.title = display.getTitle().getColoredString();
      this.root = new AdvancementWidget(this, minecraft, advancement, display);
      this.addWidget(this.root, advancement);
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }

   public String getTitle() {
      return this.title;
   }

   public void drawTab(int var1, int var2, boolean var3) {
      this.type.draw(this, var1, var2, var3, this.index);
   }

   public void drawIcon(int var1, int var2, ItemRenderer itemRenderer) {
      this.type.drawIcon(var1, var2, this.index, itemRenderer, this.icon);
   }

   public void drawContents() {
      if(!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      GlStateManager.depthFunc(518);
      fill(0, 0, 234, 113, -16777216);
      GlStateManager.depthFunc(515);
      ResourceLocation var1 = this.display.getBackground();
      if(var1 != null) {
         this.minecraft.getTextureManager().bind(var1);
      } else {
         this.minecraft.getTextureManager().bind(TextureManager.INTENTIONAL_MISSING_TEXTURE);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var2 = Mth.floor(this.scrollX);
      int var3 = Mth.floor(this.scrollY);
      int var4 = var2 % 16;
      int var5 = var3 % 16;

      for(int var6 = -1; var6 <= 15; ++var6) {
         for(int var7 = -1; var7 <= 8; ++var7) {
            blit(var4 + 16 * var6, var5 + 16 * var7, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.drawConnectivity(var2, var3, true);
      this.root.drawConnectivity(var2, var3, false);
      this.root.draw(var2, var3);
   }

   public void drawTooltips(int var1, int var2, int var3, int var4) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 0.0F, 200.0F);
      fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean var5 = false;
      int var6 = Mth.floor(this.scrollX);
      int var7 = Mth.floor(this.scrollY);
      if(var1 > 0 && var1 < 234 && var2 > 0 && var2 < 113) {
         for(AdvancementWidget var9 : this.widgets.values()) {
            if(var9.isMouseOver(var6, var7, var1, var2)) {
               var5 = true;
               var9.drawHover(var6, var7, this.fade, var3, var4);
               break;
            }
         }
      }

      GlStateManager.popMatrix();
      if(var5) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean isMouseOver(int var1, int var2, double var3, double var5) {
      return this.type.isMouseOver(var1, var2, this.index, var3, var5);
   }

   @Nullable
   public static AdvancementTab create(Minecraft minecraft, AdvancementsScreen advancementsScreen, int var2, Advancement advancement) {
      if(advancement.getDisplay() == null) {
         return null;
      } else {
         for(AdvancementTabType var7 : AdvancementTabType.values()) {
            if(var2 < var7.getMax()) {
               return new AdvancementTab(minecraft, advancementsScreen, var7, var2, advancement, advancement.getDisplay());
            }

            var2 -= var7.getMax();
         }

         return null;
      }
   }

   public void scroll(double var1, double var3) {
      if(this.maxX - this.minX > 234) {
         this.scrollX = Mth.clamp(this.scrollX + var1, (double)(-(this.maxX - 234)), 0.0D);
      }

      if(this.maxY - this.minY > 113) {
         this.scrollY = Mth.clamp(this.scrollY + var3, (double)(-(this.maxY - 113)), 0.0D);
      }

   }

   public void addAdvancement(Advancement advancement) {
      if(advancement.getDisplay() != null) {
         AdvancementWidget var2 = new AdvancementWidget(this, this.minecraft, advancement, advancement.getDisplay());
         this.addWidget(var2, advancement);
      }
   }

   private void addWidget(AdvancementWidget advancementWidget, Advancement advancement) {
      this.widgets.put(advancement, advancementWidget);
      int var3 = advancementWidget.getX();
      int var4 = var3 + 28;
      int var5 = advancementWidget.getY();
      int var6 = var5 + 27;
      this.minX = Math.min(this.minX, var3);
      this.maxX = Math.max(this.maxX, var4);
      this.minY = Math.min(this.minY, var5);
      this.maxY = Math.max(this.maxY, var6);

      for(AdvancementWidget var8 : this.widgets.values()) {
         var8.attachToParent();
      }

   }

   @Nullable
   public AdvancementWidget getWidget(Advancement advancement) {
      return (AdvancementWidget)this.widgets.get(advancement);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}
