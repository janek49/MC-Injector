package net.minecraft.client.gui.screens.advancements;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

@ClientJarOnly
public class AdvancementWidget extends GuiComponent {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final Pattern LAST_WORD = Pattern.compile("(.+) \\S+");
   private final AdvancementTab tab;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final String title;
   private final int width;
   private final List description;
   private final Minecraft minecraft;
   private AdvancementWidget parent;
   private final List children = Lists.newArrayList();
   private AdvancementProgress progress;
   private final int x;
   private final int y;

   public AdvancementWidget(AdvancementTab tab, Minecraft minecraft, Advancement advancement, DisplayInfo display) {
      this.tab = tab;
      this.advancement = advancement;
      this.display = display;
      this.minecraft = minecraft;
      this.title = minecraft.font.substrByWidth(display.getTitle().getColoredString(), 163);
      this.x = Mth.floor(display.getX() * 28.0F);
      this.y = Mth.floor(display.getY() * 27.0F);
      int var5 = advancement.getMaxCriteraRequired();
      int var6 = String.valueOf(var5).length();
      int var7 = var5 > 1?minecraft.font.width("  ") + minecraft.font.width("0") * var6 * 2 + minecraft.font.width("/"):0;
      int var8 = 29 + minecraft.font.width(this.title) + var7;
      String var9 = display.getDescription().getColoredString();
      this.description = this.findOptimalLines(var9, var8);

      for(String var11 : this.description) {
         var8 = Math.max(var8, minecraft.font.width(var11));
      }

      this.width = var8 + 3 + 5;
   }

   private List findOptimalLines(String string, int var2) {
      if(string.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> list = this.minecraft.font.split(string, var2);
         if(list.size() < 2) {
            return list;
         } else {
            String var4 = (String)list.get(0);
            String var5 = (String)list.get(1);
            int var6 = this.minecraft.font.width(var4 + ' ' + var5.split(" ")[0]);
            if(var6 - var2 <= 10) {
               return this.minecraft.font.split(string, var6);
            } else {
               Matcher var7 = LAST_WORD.matcher(var4);
               if(var7.matches()) {
                  int var8 = this.minecraft.font.width(var7.group(1));
                  if(var2 - var8 <= 10) {
                     return this.minecraft.font.split(string, var8);
                  }
               }

               return list;
            }
         }
      }
   }

   @Nullable
   private AdvancementWidget getFirstVisibleParent(Advancement advancement) {
      while(true) {
         advancement = advancement.getParent();
         if(advancement == null || advancement.getDisplay() != null) {
            break;
         }
      }

      if(advancement != null && advancement.getDisplay() != null) {
         return this.tab.getWidget(advancement);
      } else {
         return null;
      }
   }

   public void drawConnectivity(int var1, int var2, boolean var3) {
      if(this.parent != null) {
         int var4 = var1 + this.parent.x + 13;
         int var5 = var1 + this.parent.x + 26 + 4;
         int var6 = var2 + this.parent.y + 13;
         int var7 = var1 + this.x + 13;
         int var8 = var2 + this.y + 13;
         int var9 = var3?-16777216:-1;
         if(var3) {
            this.hLine(var5, var4, var6 - 1, var9);
            this.hLine(var5 + 1, var4, var6, var9);
            this.hLine(var5, var4, var6 + 1, var9);
            this.hLine(var7, var5 - 1, var8 - 1, var9);
            this.hLine(var7, var5 - 1, var8, var9);
            this.hLine(var7, var5 - 1, var8 + 1, var9);
            this.vLine(var5 - 1, var8, var6, var9);
            this.vLine(var5 + 1, var8, var6, var9);
         } else {
            this.hLine(var5, var4, var6, var9);
            this.hLine(var7, var5, var8, var9);
            this.vLine(var5, var8, var6, var9);
         }
      }

      for(AdvancementWidget var5 : this.children) {
         var5.drawConnectivity(var1, var2, var3);
      }

   }

   public void draw(int var1, int var2) {
      if(!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         float var3 = this.progress == null?0.0F:this.progress.getPercent();
         AdvancementWidgetType var4;
         if(var3 >= 1.0F) {
            var4 = AdvancementWidgetType.OBTAINED;
         } else {
            var4 = AdvancementWidgetType.UNOBTAINED;
         }

         this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         this.blit(var1 + this.x + 3, var2 + this.y, this.display.getFrame().getTexture(), 128 + var4.getIndex() * 26, 26, 26);
         Lighting.turnOnGui();
         this.minecraft.getItemRenderer().renderAndDecorateItem((LivingEntity)null, this.display.getIcon(), var1 + this.x + 8, var2 + this.y + 5);
      }

      for(AdvancementWidget var4 : this.children) {
         var4.draw(var1, var2);
      }

   }

   public void setProgress(AdvancementProgress progress) {
      this.progress = progress;
   }

   public void addChild(AdvancementWidget advancementWidget) {
      this.children.add(advancementWidget);
   }

   public void drawHover(int var1, int var2, float var3, int var4, int var5) {
      boolean var6 = var4 + var1 + this.x + this.width + 26 >= this.tab.getScreen().width;
      String var7 = this.progress == null?null:this.progress.getProgressText();
      int var8 = var7 == null?0:this.minecraft.font.width(var7);
      int var10000 = 113 - var2 - this.y - 26;
      int var10002 = this.description.size();
      this.minecraft.font.getClass();
      boolean var9 = var10000 <= 6 + var10002 * 9;
      float var10 = this.progress == null?0.0F:this.progress.getPercent();
      int var14 = Mth.floor(var10 * (float)this.width);
      AdvancementWidgetType var11;
      AdvancementWidgetType var12;
      AdvancementWidgetType var13;
      if(var10 >= 1.0F) {
         var14 = this.width / 2;
         var11 = AdvancementWidgetType.OBTAINED;
         var12 = AdvancementWidgetType.OBTAINED;
         var13 = AdvancementWidgetType.OBTAINED;
      } else if(var14 < 2) {
         var14 = this.width / 2;
         var11 = AdvancementWidgetType.UNOBTAINED;
         var12 = AdvancementWidgetType.UNOBTAINED;
         var13 = AdvancementWidgetType.UNOBTAINED;
      } else if(var14 > this.width - 2) {
         var14 = this.width / 2;
         var11 = AdvancementWidgetType.OBTAINED;
         var12 = AdvancementWidgetType.OBTAINED;
         var13 = AdvancementWidgetType.UNOBTAINED;
      } else {
         var11 = AdvancementWidgetType.OBTAINED;
         var12 = AdvancementWidgetType.UNOBTAINED;
         var13 = AdvancementWidgetType.UNOBTAINED;
      }

      int var15 = this.width - var14;
      this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      int var16 = var2 + this.y;
      int var17;
      if(var6) {
         var17 = var1 + this.x - this.width + 26 + 6;
      } else {
         var17 = var1 + this.x;
      }

      int var10001 = this.description.size();
      this.minecraft.font.getClass();
      int var18 = 32 + var10001 * 9;
      if(!this.description.isEmpty()) {
         if(var9) {
            this.render9Sprite(var17, var16 + 26 - var18, this.width, var18, 10, 200, 26, 0, 52);
         } else {
            this.render9Sprite(var17, var16, this.width, var18, 10, 200, 26, 0, 52);
         }
      }

      this.blit(var17, var16, 0, var11.getIndex() * 26, var14, 26);
      this.blit(var17 + var14, var16, 200 - var15, var12.getIndex() * 26, var15, 26);
      this.blit(var1 + this.x + 3, var2 + this.y, this.display.getFrame().getTexture(), 128 + var13.getIndex() * 26, 26, 26);
      if(var6) {
         this.minecraft.font.drawShadow(this.title, (float)(var17 + 5), (float)(var2 + this.y + 9), -1);
         if(var7 != null) {
            this.minecraft.font.drawShadow(var7, (float)(var1 + this.x - var8), (float)(var2 + this.y + 9), -1);
         }
      } else {
         this.minecraft.font.drawShadow(this.title, (float)(var1 + this.x + 32), (float)(var2 + this.y + 9), -1);
         if(var7 != null) {
            this.minecraft.font.drawShadow(var7, (float)(var1 + this.x + this.width - var8 - 5), (float)(var2 + this.y + 9), -1);
         }
      }

      if(var9) {
         for(int var19 = 0; var19 < this.description.size(); ++var19) {
            Font var21 = this.minecraft.font;
            String var23 = (String)this.description.get(var19);
            float var25 = (float)(var17 + 5);
            int var10003 = var16 + 26 - var18 + 7;
            this.minecraft.font.getClass();
            var21.draw(var23, var25, (float)(var10003 + var19 * 9), -5592406);
         }
      } else {
         for(int var19 = 0; var19 < this.description.size(); ++var19) {
            Font var22 = this.minecraft.font;
            String var24 = (String)this.description.get(var19);
            float var26 = (float)(var17 + 5);
            int var27 = var2 + this.y + 9 + 17;
            this.minecraft.font.getClass();
            var22.draw(var24, var26, (float)(var27 + var19 * 9), -5592406);
         }
      }

      Lighting.turnOnGui();
      this.minecraft.getItemRenderer().renderAndDecorateItem((LivingEntity)null, this.display.getIcon(), var1 + this.x + 8, var2 + this.y + 5);
   }

   protected void render9Sprite(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      this.blit(var1, var2, var8, var9, var5, var5);
      this.renderRepeating(var1 + var5, var2, var3 - var5 - var5, var5, var8 + var5, var9, var6 - var5 - var5, var7);
      this.blit(var1 + var3 - var5, var2, var8 + var6 - var5, var9, var5, var5);
      this.blit(var1, var2 + var4 - var5, var8, var9 + var7 - var5, var5, var5);
      this.renderRepeating(var1 + var5, var2 + var4 - var5, var3 - var5 - var5, var5, var8 + var5, var9 + var7 - var5, var6 - var5 - var5, var7);
      this.blit(var1 + var3 - var5, var2 + var4 - var5, var8 + var6 - var5, var9 + var7 - var5, var5, var5);
      this.renderRepeating(var1, var2 + var5, var5, var4 - var5 - var5, var8, var9 + var5, var6, var7 - var5 - var5);
      this.renderRepeating(var1 + var5, var2 + var5, var3 - var5 - var5, var4 - var5 - var5, var8 + var5, var9 + var5, var6 - var5 - var5, var7 - var5 - var5);
      this.renderRepeating(var1 + var3 - var5, var2 + var5, var5, var4 - var5 - var5, var8 + var6 - var5, var9 + var5, var6, var7 - var5 - var5);
   }

   protected void renderRepeating(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = 0; var9 < var3; var9 += var7) {
         int var10 = var1 + var9;
         int var11 = Math.min(var7, var3 - var9);

         for(int var12 = 0; var12 < var4; var12 += var8) {
            int var13 = var2 + var12;
            int var14 = Math.min(var8, var4 - var12);
            this.blit(var10, var13, var5, var6, var11, var14);
         }
      }

   }

   public boolean isMouseOver(int var1, int var2, int var3, int var4) {
      if(!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         int var5 = var1 + this.x;
         int var6 = var5 + 26;
         int var7 = var2 + this.y;
         int var8 = var7 + 26;
         return var3 >= var5 && var3 <= var6 && var4 >= var7 && var4 <= var8;
      } else {
         return false;
      }
   }

   public void attachToParent() {
      if(this.parent == null && this.advancement.getParent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancement);
         if(this.parent != null) {
            this.parent.addChild(this);
         }
      }

   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }
}
