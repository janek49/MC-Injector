package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class WinScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation EDITION_LOCATION = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private final boolean poem;
   private final Runnable onFinished;
   private float time;
   private List lines;
   private int totalScrollLength;
   private float scrollSpeed = 0.5F;

   public WinScreen(boolean poem, Runnable onFinished) {
      super(NarratorChatListener.NO_TITLE);
      this.poem = poem;
      this.onFinished = onFinished;
      if(!poem) {
         this.scrollSpeed = 0.75F;
      }

   }

   public void tick() {
      this.minecraft.getMusicManager().tick();
      this.minecraft.getSoundManager().tick(false);
      float var1 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      if(this.time > var1) {
         this.respawn();
      }

   }

   public void onClose() {
      this.respawn();
   }

   private void respawn() {
      this.onFinished.run();
      this.minecraft.setScreen((Screen)null);
   }

   protected void init() {
      if(this.lines == null) {
         this.lines = Lists.newArrayList();
         Resource var1 = null;

         try {
            String var2 = "" + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
            int var3 = 274;
            if(this.poem) {
               var1 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
               InputStream var4 = var1.getInputStream();
               BufferedReader var5 = new BufferedReader(new InputStreamReader(var4, StandardCharsets.UTF_8));
               Random var6 = new Random(8124371L);

               String var7;
               while((var7 = var5.readLine()) != null) {
                  String var9;
                  String var10;
                  for(var7 = var7.replaceAll("PLAYERNAME", this.minecraft.getUser().getName()); var7.contains(var2); var7 = var9 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var6.nextInt(4) + 3) + var10) {
                     int var8 = var7.indexOf(var2);
                     var9 = var7.substring(0, var8);
                     var10 = var7.substring(var8 + var2.length());
                  }

                  this.lines.addAll(this.minecraft.font.split(var7, 274));
                  this.lines.add("");
               }

               var4.close();

               for(int var8 = 0; var8 < 8; ++var8) {
                  this.lines.add("");
               }
            }

            InputStream var4 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4, StandardCharsets.UTF_8));

            String var6;
            while((var6 = var5.readLine()) != null) {
               var6 = var6.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
               var6 = var6.replaceAll("\t", "    ");
               this.lines.addAll(this.minecraft.font.split(var6, 274));
               this.lines.add("");
            }

            var4.close();
            this.totalScrollLength = this.lines.size() * 12;
         } catch (Exception var14) {
            LOGGER.error("Couldn\'t load credits", var14);
         } finally {
            IOUtils.closeQuietly(var1);
         }

      }
   }

   private void renderBg(int var1, int var2, float var3) {
      this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
      int var4 = this.width;
      float var5 = -this.time * 0.5F * this.scrollSpeed;
      float var6 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float var7 = 0.015625F;
      float var8 = this.time * 0.02F;
      float var9 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float var10 = (var9 - 20.0F - this.time) * 0.005F;
      if(var10 < var8) {
         var8 = var10;
      }

      if(var8 > 1.0F) {
         var8 = 1.0F;
      }

      var8 = var8 * var8;
      var8 = var8 * 96.0F / 255.0F;
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var12.vertex(0.0D, (double)this.height, (double)this.blitOffset).uv(0.0D, (double)(var5 * 0.015625F)).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex((double)var4, (double)this.height, (double)this.blitOffset).uv((double)((float)var4 * 0.015625F), (double)(var5 * 0.015625F)).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex((double)var4, 0.0D, (double)this.blitOffset).uv((double)((float)var4 * 0.015625F), (double)(var6 * 0.015625F)).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex(0.0D, 0.0D, (double)this.blitOffset).uv(0.0D, (double)(var6 * 0.015625F)).color(var8, var8, var8, 1.0F).endVertex();
      var11.end();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBg(var1, var2, var3);
      int var4 = 274;
      int var5 = this.width / 2 - 137;
      int var6 = this.height + 50;
      this.time += var3;
      float var7 = -this.time * this.scrollSpeed;
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, var7, 0.0F);
      this.minecraft.getTextureManager().bind(LOGO_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.blit(var5, var6, 0, 0, 155, 44);
      this.blit(var5 + 155, var6, 0, 45, 155, 44);
      this.minecraft.getTextureManager().bind(EDITION_LOCATION);
      blit(var5 + 88, var6 + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      GlStateManager.disableAlphaTest();
      int var8 = var6 + 100;

      for(int var9 = 0; var9 < this.lines.size(); ++var9) {
         if(var9 == this.lines.size() - 1) {
            float var10 = (float)var8 + var7 - (float)(this.height / 2 - 6);
            if(var10 < 0.0F) {
               GlStateManager.translatef(0.0F, -var10, 0.0F);
            }
         }

         if((float)var8 + var7 + 12.0F + 8.0F > 0.0F && (float)var8 + var7 < (float)this.height) {
            String var10 = (String)this.lines.get(var9);
            if(var10.startsWith("[C]")) {
               this.font.drawShadow(var10.substring(3), (float)(var5 + (274 - this.font.width(var10.substring(3))) / 2), (float)var8, 16777215);
            } else {
               this.font.random.setSeed((long)((float)((long)var9 * 4238972211L) + this.time / 4.0F));
               this.font.drawShadow(var10, (float)var5, (float)var8, 16777215);
            }
         }

         var8 += 12;
      }

      GlStateManager.popMatrix();
      this.minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      int var9 = this.width;
      int var10 = this.height;
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var12.vertex(0.0D, (double)var10, (double)this.blitOffset).uv(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex((double)var9, (double)var10, (double)this.blitOffset).uv(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex((double)var9, 0.0D, (double)this.blitOffset).uv(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex(0.0D, 0.0D, (double)this.blitOffset).uv(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var11.end();
      GlStateManager.disableBlend();
      super.render(var1, var2, var3);
   }
}
