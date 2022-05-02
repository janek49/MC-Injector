package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

@ClientJarOnly
public class PlayerTabOverlay extends GuiComponent {
   private static final Ordering PLAYER_ORDERING = Ordering.from(new PlayerTabOverlay.PlayerInfoComparator());
   private final Minecraft minecraft;
   private final Gui gui;
   private Component footer;
   private Component header;
   private long visibilityId;
   private boolean visible;

   public PlayerTabOverlay(Minecraft minecraft, Gui gui) {
      this.minecraft = minecraft;
      this.gui = gui;
   }

   public Component getNameForDisplay(PlayerInfo playerInfo) {
      return playerInfo.getTabListDisplayName() != null?playerInfo.getTabListDisplayName():PlayerTeam.formatNameForTeam(playerInfo.getTeam(), new TextComponent(playerInfo.getProfile().getName()));
   }

   public void setVisible(boolean visible) {
      if(visible && !this.visible) {
         this.visibilityId = Util.getMillis();
      }

      this.visible = visible;
   }

   public void render(int var1, Scoreboard scoreboard, @Nullable Objective objective) {
      ClientPacketListener var4 = this.minecraft.player.connection;
      List<PlayerInfo> var5 = PLAYER_ORDERING.sortedCopy(var4.getOnlinePlayers());
      int var6 = 0;
      int var7 = 0;

      for(PlayerInfo var9 : var5) {
         int var10 = this.minecraft.font.width(this.getNameForDisplay(var9).getColoredString());
         var6 = Math.max(var6, var10);
         if(objective != null && objective.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            var10 = this.minecraft.font.width(" " + scoreboard.getOrCreatePlayerScore(var9.getProfile().getName(), objective).getScore());
            var7 = Math.max(var7, var10);
         }
      }

      var5 = var5.subList(0, Math.min(var5.size(), 80));
      int var8 = var5.size();
      int var9 = var8;

      int var10;
      for(var10 = 1; var9 > 20; var9 = (var8 + var10 - 1) / var10) {
         ++var10;
      }

      boolean var11 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int var12;
      if(objective != null) {
         if(objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            var12 = 90;
         } else {
            var12 = var7;
         }
      } else {
         var12 = 0;
      }

      int var13 = Math.min(var10 * ((var11?9:0) + var6 + var12 + 13), var1 - 50) / var10;
      int var14 = var1 / 2 - (var13 * var10 + (var10 - 1) * 5) / 2;
      int var15 = 10;
      int var16 = var13 * var10 + (var10 - 1) * 5;
      List<String> var17 = null;
      if(this.header != null) {
         var17 = this.minecraft.font.split(this.header.getColoredString(), var1 - 50);

         for(String var19 : var17) {
            var16 = Math.max(var16, this.minecraft.font.width(var19));
         }
      }

      List<String> var18 = null;
      if(this.footer != null) {
         var18 = this.minecraft.font.split(this.footer.getColoredString(), var1 - 50);

         for(String var20 : var18) {
            var16 = Math.max(var16, this.minecraft.font.width(var20));
         }
      }

      if(var17 != null) {
         int var10000 = var1 / 2 - var16 / 2 - 1;
         int var10001 = var15 - 1;
         int var10002 = var1 / 2 + var16 / 2 + 1;
         int var10004 = var17.size();
         this.minecraft.font.getClass();
         fill(var10000, var10001, var10002, var15 + var10004 * 9, Integer.MIN_VALUE);

         for(String var20 : var17) {
            int var21 = this.minecraft.font.width(var20);
            this.minecraft.font.drawShadow(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
            this.minecraft.font.getClass();
            var15 += 9;
         }

         ++var15;
      }

      fill(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var9 * 9, Integer.MIN_VALUE);
      int var19 = this.minecraft.options.getBackgroundColor(553648127);

      for(int var20 = 0; var20 < var8; ++var20) {
         int var21 = var20 / var9;
         int var22 = var20 % var9;
         int var23 = var14 + var21 * var13 + var21 * 5;
         int var24 = var15 + var22 * 9;
         fill(var23, var24, var23 + var13, var24 + 8, var19);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if(var20 < var5.size()) {
            PlayerInfo var25 = (PlayerInfo)var5.get(var20);
            GameProfile var26 = var25.getProfile();
            if(var11) {
               Player var27 = this.minecraft.level.getPlayerByUUID(var26.getId());
               boolean var28 = var27 != null && var27.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(var26.getName()) || "Grumm".equals(var26.getName()));
               this.minecraft.getTextureManager().bind(var25.getSkinLocation());
               int var29 = 8 + (var28?8:0);
               int var30 = 8 * (var28?-1:1);
               GuiComponent.blit(var23, var24, 8, 8, 8.0F, (float)var29, 8, var30, 64, 64);
               if(var27 != null && var27.isModelPartShown(PlayerModelPart.HAT)) {
                  int var31 = 8 + (var28?8:0);
                  int var32 = 8 * (var28?-1:1);
                  GuiComponent.blit(var23, var24, 8, 8, 40.0F, (float)var31, 8, var32, 64, 64);
               }

               var23 += 9;
            }

            String var27 = this.getNameForDisplay(var25).getColoredString();
            if(var25.getGameMode() == GameType.SPECTATOR) {
               this.minecraft.font.drawShadow(ChatFormatting.ITALIC + var27, (float)var23, (float)var24, -1862270977);
            } else {
               this.minecraft.font.drawShadow(var27, (float)var23, (float)var24, -1);
            }

            if(objective != null && var25.getGameMode() != GameType.SPECTATOR) {
               int var28 = var23 + var6 + 1;
               int var29 = var28 + var12;
               if(var29 - var28 > 5) {
                  this.renderTablistScore(objective, var24, var26.getName(), var28, var29, var25);
               }
            }

            this.renderPingIcon(var13, var23 - (var11?9:0), var24, var25);
         }
      }

      if(var18 != null) {
         var15 = var15 + var9 * 9 + 1;
         int var52 = var1 / 2 - var16 / 2 - 1;
         int var53 = var15 - 1;
         int var54 = var1 / 2 + var16 / 2 + 1;
         int var55 = var18.size();
         this.minecraft.font.getClass();
         fill(var52, var53, var54, var15 + var55 * 9, Integer.MIN_VALUE);

         for(String var21 : var18) {
            int var22 = this.minecraft.font.width(var21);
            this.minecraft.font.drawShadow(var21, (float)(var1 / 2 - var22 / 2), (float)var15, -1);
            this.minecraft.font.getClass();
            var15 += 9;
         }
      }

   }

   protected void renderPingIcon(int var1, int var2, int var3, PlayerInfo playerInfo) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
      int var5 = 0;
      int var6;
      if(playerInfo.getLatency() < 0) {
         var6 = 5;
      } else if(playerInfo.getLatency() < 150) {
         var6 = 0;
      } else if(playerInfo.getLatency() < 300) {
         var6 = 1;
      } else if(playerInfo.getLatency() < 600) {
         var6 = 2;
      } else if(playerInfo.getLatency() < 1000) {
         var6 = 3;
      } else {
         var6 = 4;
      }

      this.blitOffset += 100;
      this.blit(var2 + var1 - 11, var3, 0, 176 + var6 * 8, 10, 8);
      this.blitOffset -= 100;
   }

   private void renderTablistScore(Objective objective, int var2, String string, int var4, int var5, PlayerInfo playerInfo) {
      int var7 = objective.getScoreboard().getOrCreatePlayerScore(string, objective).getScore();
      if(objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         long var8 = Util.getMillis();
         if(this.visibilityId == playerInfo.getRenderVisibilityId()) {
            if(var7 < playerInfo.getLastHealth()) {
               playerInfo.setLastHealthTime(var8);
               playerInfo.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 20));
            } else if(var7 > playerInfo.getLastHealth()) {
               playerInfo.setLastHealthTime(var8);
               playerInfo.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 10));
            }
         }

         if(var8 - playerInfo.getLastHealthTime() > 1000L || this.visibilityId != playerInfo.getRenderVisibilityId()) {
            playerInfo.setLastHealth(var7);
            playerInfo.setDisplayHealth(var7);
            playerInfo.setLastHealthTime(var8);
         }

         playerInfo.setRenderVisibilityId(this.visibilityId);
         playerInfo.setLastHealth(var7);
         int var10 = Mth.ceil((float)Math.max(var7, playerInfo.getDisplayHealth()) / 2.0F);
         int var11 = Math.max(Mth.ceil((float)(var7 / 2)), Math.max(Mth.ceil((float)(playerInfo.getDisplayHealth() / 2)), 10));
         boolean var12 = playerInfo.getHealthBlinkTime() > (long)this.gui.getGuiTicks() && (playerInfo.getHealthBlinkTime() - (long)this.gui.getGuiTicks()) / 3L % 2L == 1L;
         if(var10 > 0) {
            int var13 = Mth.floor(Math.min((float)(var5 - var4 - 4) / (float)var11, 9.0F));
            if(var13 > 3) {
               for(int var14 = var10; var14 < var11; ++var14) {
                  this.blit(var4 + var14 * var13, var2, var12?25:16, 0, 9, 9);
               }

               for(int var14 = 0; var14 < var10; ++var14) {
                  this.blit(var4 + var14 * var13, var2, var12?25:16, 0, 9, 9);
                  if(var12) {
                     if(var14 * 2 + 1 < playerInfo.getDisplayHealth()) {
                        this.blit(var4 + var14 * var13, var2, 70, 0, 9, 9);
                     }

                     if(var14 * 2 + 1 == playerInfo.getDisplayHealth()) {
                        this.blit(var4 + var14 * var13, var2, 79, 0, 9, 9);
                     }
                  }

                  if(var14 * 2 + 1 < var7) {
                     this.blit(var4 + var14 * var13, var2, var14 >= 10?160:52, 0, 9, 9);
                  }

                  if(var14 * 2 + 1 == var7) {
                     this.blit(var4 + var14 * var13, var2, var14 >= 10?169:61, 0, 9, 9);
                  }
               }
            } else {
               float var14 = Mth.clamp((float)var7 / 20.0F, 0.0F, 1.0F);
               int var15 = (int)((1.0F - var14) * 255.0F) << 16 | (int)(var14 * 255.0F) << 8;
               String var16 = "" + (float)var7 / 2.0F;
               if(var5 - this.minecraft.font.width(var16 + "hp") >= var4) {
                  var16 = var16 + "hp";
               }

               this.minecraft.font.drawShadow(var16, (float)((var5 + var4) / 2 - this.minecraft.font.width(var16) / 2), (float)var2, var15);
            }
         }
      } else {
         String var8 = ChatFormatting.YELLOW + "" + var7;
         this.minecraft.font.drawShadow(var8, (float)(var5 - this.minecraft.font.width(var8)), (float)var2, 16777215);
      }

   }

   public void setFooter(@Nullable Component footer) {
      this.footer = footer;
   }

   public void setHeader(@Nullable Component header) {
      this.header = header;
   }

   public void reset() {
      this.header = null;
      this.footer = null;
   }

   @ClientJarOnly
   static class PlayerInfoComparator implements Comparator {
      private PlayerInfoComparator() {
      }

      public int compare(PlayerInfo var1, PlayerInfo var2) {
         PlayerTeam var3 = var1.getTeam();
         PlayerTeam var4 = var2.getTeam();
         return ComparisonChain.start().compareTrueFirst(var1.getGameMode() != GameType.SPECTATOR, var2.getGameMode() != GameType.SPECTATOR).compare(var3 != null?var3.getName():"", var4 != null?var4.getName():"").compare(var1.getProfile().getName(), var2.getProfile().getName(), String::compareToIgnoreCase).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((PlayerInfo)var1, (PlayerInfo)var2);
      }
   }
}
