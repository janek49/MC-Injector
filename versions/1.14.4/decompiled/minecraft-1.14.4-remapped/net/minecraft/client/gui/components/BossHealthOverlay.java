package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

@ClientJarOnly
public class BossHealthOverlay extends GuiComponent {
   private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft minecraft;
   private final Map events = Maps.newLinkedHashMap();

   public BossHealthOverlay(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render() {
      if(!this.events.isEmpty()) {
         int var1 = this.minecraft.window.getGuiScaledWidth();
         int var2 = 12;

         for(LerpingBossEvent var4 : this.events.values()) {
            int var5 = var1 / 2 - 91;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
            this.drawBar(var5, var2, var4);
            String var7 = var4.getName().getColoredString();
            int var8 = this.minecraft.font.width(var7);
            int var9 = var1 / 2 - var8 / 2;
            int var10 = var2 - 9;
            this.minecraft.font.drawShadow(var7, (float)var9, (float)var10, 16777215);
            this.minecraft.font.getClass();
            var2 += 10 + 9;
            if(var2 >= this.minecraft.window.getGuiScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void drawBar(int var1, int var2, BossEvent bossEvent) {
      this.blit(var1, var2, 0, bossEvent.getColor().ordinal() * 5 * 2, 182, 5);
      if(bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
         this.blit(var1, var2, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int var4 = (int)(bossEvent.getPercent() * 183.0F);
      if(var4 > 0) {
         this.blit(var1, var2, 0, bossEvent.getColor().ordinal() * 5 * 2 + 5, var4, 5);
         if(bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(var1, var2, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5, var4, 5);
         }
      }

   }

   public void update(ClientboundBossEventPacket clientboundBossEventPacket) {
      if(clientboundBossEventPacket.getOperation() == ClientboundBossEventPacket.Operation.ADD) {
         this.events.put(clientboundBossEventPacket.getId(), new LerpingBossEvent(clientboundBossEventPacket));
      } else if(clientboundBossEventPacket.getOperation() == ClientboundBossEventPacket.Operation.REMOVE) {
         this.events.remove(clientboundBossEventPacket.getId());
      } else {
         ((LerpingBossEvent)this.events.get(clientboundBossEventPacket.getId())).update(clientboundBossEventPacket);
      }

   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if(!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if(var2.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if(!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if(var2.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if(!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if(var2.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
