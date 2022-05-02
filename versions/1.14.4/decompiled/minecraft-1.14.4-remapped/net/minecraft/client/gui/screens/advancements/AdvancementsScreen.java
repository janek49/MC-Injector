package net.minecraft.client.gui.screens.advancements;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
   private final ClientAdvancements advancements;
   private final Map tabs = Maps.newLinkedHashMap();
   private AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(ClientAdvancements advancements) {
      super(NarratorChatListener.NO_TITLE);
      this.advancements = advancements;
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if(this.selectedTab == null && !this.tabs.isEmpty()) {
         this.advancements.setSelectedTab(((AdvancementTab)this.tabs.values().iterator().next()).getAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null?null:this.selectedTab.getAdvancement(), true);
      }

   }

   public void removed() {
      this.advancements.setListener((ClientAdvancements.Listener)null);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if(var1 != null) {
         var1.send((Packet)ServerboundSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(var5 == 0) {
         int var6 = (this.width - 252) / 2;
         int var7 = (this.height - 140) / 2;

         for(AdvancementTab var9 : this.tabs.values()) {
            if(var9.isMouseOver(var6, var7, var1, var3)) {
               this.advancements.setSelectedTab(var9.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(this.minecraft.options.keyAdvancements.matches(var1, var2)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(int var1, int var2, float var3) {
      int var4 = (this.width - 252) / 2;
      int var5 = (this.height - 140) / 2;
      this.renderBackground();
      this.renderInside(var1, var2, var4, var5);
      this.renderWindow(var4, var5);
      this.renderTooltips(var1, var2, var4, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if(var5 != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if(!this.isScrolling) {
            this.isScrolling = true;
         } else if(this.selectedTab != null) {
            this.selectedTab.scroll(var6, var8);
         }

         return true;
      }
   }

   private void renderInside(int var1, int var2, int var3, int var4) {
      AdvancementTab var5 = this.selectedTab;
      if(var5 == null) {
         fill(var3 + 9, var4 + 18, var3 + 9 + 234, var4 + 18 + 113, -16777216);
         String var6 = I18n.get("advancements.empty", new Object[0]);
         int var7 = this.font.width(var6);
         Font var10000 = this.font;
         float var10002 = (float)(var3 + 9 + 117 - var7 / 2);
         int var10003 = var4 + 18 + 56;
         this.font.getClass();
         var10000.draw(var6, var10002, (float)(var10003 - 9 / 2), -1);
         var10000 = this.font;
         var10002 = (float)(var3 + 9 + 117 - this.font.width(":(") / 2);
         var10003 = var4 + 18 + 113;
         this.font.getClass();
         var10000.draw(":(", var10002, (float)(var10003 - 9), -1);
      } else {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(var3 + 9), (float)(var4 + 18), -400.0F);
         GlStateManager.enableDepthTest();
         var5.drawContents();
         GlStateManager.popMatrix();
         GlStateManager.depthFunc(515);
         GlStateManager.disableDepthTest();
      }
   }

   public void renderWindow(int var1, int var2) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      Lighting.turnOff();
      this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
      this.blit(var1, var2, 0, 0, 252, 140);
      if(this.tabs.size() > 1) {
         this.minecraft.getTextureManager().bind(TABS_LOCATION);

         for(AdvancementTab var4 : this.tabs.values()) {
            var4.drawTab(var1, var2, var4 == this.selectedTab);
         }

         GlStateManager.enableRescaleNormal();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         Lighting.turnOnGui();

         for(AdvancementTab var4 : this.tabs.values()) {
            var4.drawIcon(var1, var2, this.itemRenderer);
         }

         GlStateManager.disableBlend();
      }

      this.font.draw(I18n.get("gui.advancements", new Object[0]), (float)(var1 + 8), (float)(var2 + 6), 4210752);
   }

   private void renderTooltips(int var1, int var2, int var3, int var4) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if(this.selectedTab != null) {
         GlStateManager.pushMatrix();
         GlStateManager.enableDepthTest();
         GlStateManager.translatef((float)(var3 + 9), (float)(var4 + 18), 400.0F);
         this.selectedTab.drawTooltips(var1 - var3 - 9, var2 - var4 - 18, var3, var4);
         GlStateManager.disableDepthTest();
         GlStateManager.popMatrix();
      }

      if(this.tabs.size() > 1) {
         for(AdvancementTab var6 : this.tabs.values()) {
            if(var6.isMouseOver(var3, var4, (double)var1, (double)var2)) {
               this.renderTooltip(var6.getTitle(), var1, var2);
            }
         }
      }

   }

   public void onAddAdvancementRoot(Advancement advancement) {
      AdvancementTab var2 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), advancement);
      if(var2 != null) {
         this.tabs.put(advancement, var2);
      }
   }

   public void onRemoveAdvancementRoot(Advancement advancement) {
   }

   public void onAddAdvancementTask(Advancement advancement) {
      AdvancementTab var2 = this.getTab(advancement);
      if(var2 != null) {
         var2.addAdvancement(advancement);
      }

   }

   public void onRemoveAdvancementTask(Advancement advancement) {
   }

   public void onUpdateAdvancementProgress(Advancement advancement, AdvancementProgress advancementProgress) {
      AdvancementWidget var3 = this.getAdvancementWidget(advancement);
      if(var3 != null) {
         var3.setProgress(advancementProgress);
      }

   }

   public void onSelectedTabChanged(@Nullable Advancement advancement) {
      this.selectedTab = (AdvancementTab)this.tabs.get(advancement);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementWidget getAdvancementWidget(Advancement advancement) {
      AdvancementTab var2 = this.getTab(advancement);
      return var2 == null?null:var2.getWidget(advancement);
   }

   @Nullable
   private AdvancementTab getTab(Advancement advancement) {
      while(advancement.getParent() != null) {
         advancement = advancement.getParent();
      }

      return (AdvancementTab)this.tabs.get(advancement);
   }
}
